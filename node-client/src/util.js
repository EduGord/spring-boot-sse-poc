function generatePaymentDetails() {
    return {
        amount: Math.floor(Math.random() * 1000) + 1,
        holder: generateHolder(),
        expiryDate: generateExpiryDate(),
        cardNumber: generateCardNumber(),
        cvv: generateCvv()
    };
}

function generateRandomString() {
    return Math.random().toString(36).substring(7);
}

function generateHolder() {
    return `${Math.random().toString(36).substring(7)} ${Math.random().toString(36).substring(7)}`;
}

function generateExpiryDate() {
    return `${Math.floor(Math.random() * 12) + 1}${Math.floor(Math.random() * 10)}/20${Math.floor(Math.random() * 10)}${Math.floor(Math.random() * 10)}`;
}

function generateCardNumber() {
    return `${Math.floor(Math.random() * 10000)} ${Math.floor(Math.random() * 10000)} ${Math.floor(Math.random() * 10000)} ${Math.floor(Math.random() * 10000)}`;
}

function generateCvv() {
    return Math.floor(Math.random() * 10000);
}

module.exports = {
    generateRandomString,
    generatePaymentDetails
};
