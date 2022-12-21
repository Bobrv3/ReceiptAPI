# ReceiptApp
CRUD Restful API that implements the functionality of forming receipt in the store

## Deployment
1. Clone a repository with a project:<br>
   <code>git clone https://github.com/Bobrv3/ReceiptAPI.git</code>
2. In the root of the project (where the docker-compose file is located), run the command:<br>
   <code>docker-compose --env-file .env.default up</code>
3. Wait until the application is deployed.
4. To check the success of the application start, and get the txt receipt:<br>
   <code>http://localhost:8080/api/v1/orders/1/receipt</code>
   The download of the file with the .txt extension will begin

Default values when starting the application:
+ server port: 8080
+ db user name: test_user
+ db password: 1234
+ db port: 5432
+ db name: receipt_db

To change the default values, you need to change the corresponding variables in the <a href=https://github.com/Bobrv3/ReceiptAPI/blob/main/.env.default>.env.default</a> file

## Endpoints
### Order
+ <pre>GET       /api/v1/orders/{id}	  Getting a specific order by id</pre>
+ <pre>GET       /api/v1/orders/{id}/receipt	  Getting a receipt file by order id</pre>
+ <pre>GET       /api/v1/orders/	      Getting a Page of all orders</pre>
  <pre>parameters (optional):
    - offset   zero-based page index, must not be negative
    - limit    the size of the page to be returned, must be greater than 0</pre>
+ <pre>POST      /api/v1/orders/	      Registration (creation) of a new order</pre>
+ <pre>PUT       /api/v1/orders/{id}	  Changing information about an existing order</pre>
+ <pre>DELETE    /api/v1/orders/{id}	  Deleting a order</pre><br>
### Discount card
+ <pre>GET       /api/v1/discount-cards/{id}	  Getting a specific discount card by id</pre>
+ <pre>GET       /api/v1/discount-cards/	      Getting a Page of all discount cards</pre>
  <pre>parameters (optional):
    - offset   zero-based page index, must not be negative
    - limit    the size of the page to be returned, must be greater than 0</pre>
+ <pre>POST      /api/v1/discount-cards/	      Registration (creation) of a new discount card</pre>
+ <pre>PUT       /api/v1/discount-cards/{id}	  Changing information about an existing discount card</pre>
+ <pre>DELETE    /api/v1/discount-cards/{id}	  Deleting a discount card</pre><br>
### Product
+ <pre>GET       /api/v1/products/{id}	  Getting a specific product by id</pre>
+ <pre>GET       /api/v1/products/	      Getting a Page of all products</pre>
  <pre>parameters (optional):
    - offset   zero-based page index, must not be negative
    - limit    the size of the page to be returned, must be greater than 0</pre>
+ <pre>POST      /api/v1/products/	      Registration (creation) of a new product</pre>
+ <pre>PUT       /api/v1/products/{id}	  Changing information about an existing product</pre>
+ <pre>DELETE    /api/v1/products/{id}	  Deleting a product</pre><br>
### Sale
+ <pre>GET       /api/v1/sales/{id}	  Getting a specific sale by id</pre>
+ <pre>GET       /api/v1/sales/	      Getting a Page of all sales</pre>
  <pre>parameters (optional):
    - offset   zero-based page index, must not be negative
    - limit    the size of the page to be returned, must be greater than 0</pre>
+ <pre>POST      /api/v1/sales/	      Registration (creation) of a new sale</pre>
+ <pre>PUT       /api/v1/sales/{id}	  Changing information about an existing sale</pre>
+ <pre>DELETE    /api/v1/sales/{id}	  Deleting a sale</pre><br>

### Database schema
<p align="center">
<img src="https://github.com/Bobrv3/ReceiptAPI/blob/main/ReceiptAPI/src/main/resources/db_schema.png" width="1000" title="hover text">
</p>
