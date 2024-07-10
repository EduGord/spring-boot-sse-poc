const axios = require('axios');
const EventSource = require('eventsource');
const util = require('./util');

const API_BASE_URL = process.env.API_BASE_URL;

async function signIn(username, password) {
    try {
        const response = await axios.post(`${API_BASE_URL}/auth/sign-in`, {
            username: username,
            password: password
        });
        return response.data;
    } catch (error) {
        console.error('Error signing in:', error);
        process.exit(1);
    }
}

async function initiatePayment(token) {
    try {
        const payload = util.generatePaymentDetails()
        const response = await axios.post(`${API_BASE_URL}/payment/pay`, payload, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
        return response.data.paymentId;
    } catch (error) {
        console.error('Error initiating payment:', error);
        process.exit(1);
    }
}

async function listenForPayments(token, paymentId) {
    const eventSource = new EventSource(`${API_BASE_URL}/payment/status/${paymentId}`, {
        headers: {
            Authorization: `Bearer ${token}`
        }
    });

    return new Promise((resolve, reject) => {
        eventSource.onmessage = (event) => {
            const data = JSON.parse(event.data);
            console.log('Received event:', data);
            resolve(data);
            eventSource.close();
        };

        eventSource.onerror = (error) => {
            console.error('Error with event source:', error);
            reject(error);
            eventSource.close();
        };
    });
}

module.exports = {
    signIn,
    initiatePayment,
    listenForPayments
};
