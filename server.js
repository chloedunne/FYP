const express = require("express");
const app = express();

const stripe = require("stripe")("sk_test_51IIYYWLkp6zbh2pAtHjCB2ZSxiylajVcmvfqwmQlVC1dyzc4KSIpm3JQb83GHPzRF9R6MniVUt41U07H12S9SjjN00iz8WX5gK");
app.use(express.static("."));
app.use(express.json());
const calculateOrderAmount = items => {
  console.log(items[0].amount)
  return items[0].amount;
};
app.post("/create-payment-intent", async (req, res) => {
  const { items } = req.body;
  const { currency } = req.body;

  const paymentIntent = await stripe.paymentIntents.create({
    amount: calculateOrderAmount(items),
    currency: currency
  });
  res.send({
    clientSecret: paymentIntent.client_secret
  });
});
app.get("/test", async (req, res) => {
   res.send("test is working");
  });
app.listen(4242, () => console.log('Node server listening on port 4242!'));
