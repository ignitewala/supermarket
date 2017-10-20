# supermarket
Simulate Super market

 <u><b>Description:</b></u> The checkout process will scan each item and get
  the price, finally apply discount. It will have a pre-configured inventory
  with item prices. The scanning process will retrieve price from inventory.
  Each item will have a serialnumber, id, description
  <ul>
  ID = 001, SER No = 1001, Desc = 'Classic butter'
  </ul>
  System will have Offers/deals as a sequence of rules. <br>
  <br>
  <b><u>Assumptions</u></b>: each deal will be associated with only one
  product, such as product A will have its own deal.Product 'B' will have its
  own rule <b><i>It will not combine 'A' and 'B' </i></b>such as buy 2 pieces
  of item 'A' and 3 pieces of item 'B' then get 60% off. <br>
  Each product can have many rules such as - buy 2 get 30% off, buy 3 get 40%
  off, buy 5 get 50% etc. System will apply the maximum possible discount - for
  example if one buys 5 items then instead of giving 30% on 2 items and 40% on
  remaining 3 items, it will calculate flat 50% discount
