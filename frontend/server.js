// Hotel-Booking/frontend/server.js
const express = require('express');
const axios = require('axios');
const http = require('http');
const socketIo = require('socket.io');

const app = express();
const server = http.createServer(app);
const io = socketIo(server);

app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Serve static files
app.use(express.static('public'));

const bookingUrl = process.env.BOOKING_SERVICE_URL || 'http://localhost:8081';
const paymentUrl = process.env.PAYMENT_SERVICE_URL || 'http://localhost:8082';
// Booking route
app.post('/api/bookings', async (req, res) => {
  try {
    const payload = {
      id: req.body.bookingId,
      userId: req.body.userId,
      hotelId: req.body.hotelId,
      checkInDate: req.body.checkInDate,
      checkOutDate: req.body.checkOutDate
    };
    console.log('Creating booking', JSON.stringify(payload));
    const bookingResponse = await axios.post(`${bookingUrl}/api/bookings/book`, payload, {
      headers: {
        'Content-Type': 'application/json'
      }
    });
    if (bookingResponse.status === 200 && bookingResponse.data.status === 'PENDING PAYMENT') {
      console.log('Booking created successfully', bookingResponse.data);
      res.redirect(`/payment?bookingId=${bookingResponse.data.id}`);
    } else {
      res.status(500).send('Error creating booking : ' + bookingResponse.data.status);

    }
  } catch (error) {
    console.error('Error creating booking', error);
    res.status(error.status).send(error.response.data);
  }
});

app.post('/api/payments', async (req, res) => {
  try {
    const payload = {
      cardNumber: req.body.cardNumber,
      cvv: req.body.cvv,
      cardHolder: req.body.cardHolder,
      amount: req.body.amount,
      expiryDate: req.body.expiryDate,
      bookingId: req.body.bookingId
    };
    console.log('Creating payment', JSON.stringify(payload));
    const paymentResponse = await axios.post(`${paymentUrl}/api/payments`, payload, {
      headers: {
        'Content-Type': 'application/json'
      }
    });
    // if the payment is not accepted, return an error
    if (paymentResponse.status !== 202) {
      throw new Error('Error creating payment');
    }
    console.log('Payment created successfully', paymentResponse.data);
    //res.redirect(`/api/payments/${paymentResponse.data}`);
    res.status(200).json({paymentId : paymentResponse.data});
  } catch (error) {
    console.error('Error creating payment', error);
    res.status(error.response ? error.response.status : 500).send('Error creating payment');
  }
});

app.get('/api/payments/:id', async (req, res) => {
  try {
    const paymentId = req.params.id;
    console.log('Fetching payment status for', paymentId);
    const paymentResponse = await axios.get(`${paymentUrl}/api/payments/${paymentId}`);
    console.log('Payment status received:', paymentResponse.data);
    if (paymentResponse.status === 200) {
      // res.redirect('/status');
      res.status(200).json(paymentResponse.data);
    } else {
      res.status(500).send('Error fetching payment status');
    }
  } catch (error) {
    console.error('Error fetching payment status', error);
    res.status(error.response ? error.response.status : 500).send('Error fetching payment status');
  }
});





// Route to test GET request
app.get('/api/all-bookings', async (req, res) => {
  try {
    const response = await axios.get(`${bookingUrl}/api/bookings`);
    console.log('GET response:', response.data);
    res.status(200).json(response.data);
  } catch (error) {
    console.error('Error fetching bookings', error.response ? error.response.data : error.message);
    res.status(error.response ? error.response.status : 500).send('Error fetching bookings');
  }
});

// Payment route
app.get('/payment', (req, res) => {
  res.sendFile(__dirname + '/public/payment.html');
});

// Status route
app.get('/status', (req, res) => {
  res.sendFile(__dirname + '/public/status.html');
});

// WebSocket connection
io.on('connection', (socket) => {
  console.log('New client connected');

  // Simulate async status updates
  setTimeout(() => {
    socket.emit('paymentStatus', { status: 'Payment Successful' });
    socket.emit('bookingStatus', { status: 'Booking Confirmed' });
  }, 5000);

  socket.on('disconnect', () => {
    console.log('Client disconnected');
  });
});

const PORT = process.env.PORT || 3000;
server.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});