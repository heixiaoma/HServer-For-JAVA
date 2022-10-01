const express = require("express");
const app = express();

// This is the callback function that takes calls from JVM.
var test = (count) => {
    console.log(`Call #${count}`);
}

// This is the express handler.
app.get('/', function (req, res) {
    res.send('Hello');
    console.log('GET /');
})

// Start the express server.
const server = app.listen(8991, "0.0.0.0", () => {
    const host = server.address().address;
    const port = server.address().port;
    console.log(`Listening at http://${host}:${port}`);
});
