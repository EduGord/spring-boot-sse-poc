require('dotenv').config();

const USERNAME = process.env.SSE_POC_USERNAME;
const PASSWORD = process.env.SSE_POC_PASSWORD;
const TIME_TO_WAIT_BEFORE_START_LISTENING = 5000;

const util = require('./util');
const client = require('./client');


async function test() {
    console.log('Testing with immediate listener');
    const token = await client.signIn(USERNAME, PASSWORD);
    const paymentId = await client.initiatePayment(token);
    await client.listenForPayments(token, paymentId);
    console.log('Immediate listener test completed');
}

async function testWithDelayedListener() {
    console.log('Testing with delayed listener');
    const token = await client.signIn(USERNAME, PASSWORD);
    const paymentId = await client.initiatePayment(token);
    await new Promise((resolve) => {
        setTimeout(async () => {
            await client.listenForPayments(token, paymentId);
            resolve();
        }, TIME_TO_WAIT_BEFORE_START_LISTENING);
    });
    console.log('Delayed listener test completed');
}

async function testUnauthorizedWhenTryingToSubscribeToPaymentOfAnotherUser() {
    console.log('Testing unauthorized when trying to subscribe to payment of another user');
    const token = await client.signIn(USERNAME, PASSWORD);
    const paymentId = await client.initiatePayment(token);
    const anotherToken = await client.signIn(util.generateRandomString(), util.generateRandomString());
    try {
        await client.listenForPayments(anotherToken, paymentId);
    } catch (error) {
        console.log('Unauthorized error:', error);
    }
    console.log('Unauthorized test completed');

}

async function main() {
    await test();
    await testWithDelayedListener();
    await testUnauthorizedWhenTryingToSubscribeToPaymentOfAnotherUser();
}

main().then(() => console.log('End of tests'));