# Food Delivery

An app that mimics a food delivery service

## Functionality 

The following **required** functionality is completed:

* [x] User can start the app
* [x] User can enter their email and name on the sign-up screen
* [x] User can upload a profile image from the gallery or camera on the sign-up screen
* [x] Once signed up, the user is directed to the home screen
* [x] Navigation drawer displays the user's profile picture, name, and email
* [x] Navigation drawer has buttons for Home and Recent Orders, and a sign-out button
* [x] Clicking on Recent Orders in the Navigation Drawer takes the user to the Recent Orders screen
* [x] Home Screen has a search icon in the app bar for searching restaurants
* [x] Home Screen is divided into two fragments:
  * [x] Fragment 1 displays recent restaurants in horizontally scrollable cards
    * [x] Minimum of 5 pre-added recent restaurants
    * [x] Clicking on a restaurant navigates the user to the Restaurant screen
  * [x] Fragment 2 displays all restaurants in vertically scrollable cards
    * [x] Minimum of 8 pre-added restaurants
    * [x] Clicking on a restaurant navigates the user to the Restaurant screen
    * [x] Each restaurant has its own location within 50 miles of the user
    * [x] Restaurant collection in Firestore contains name, location, and three images stored in Firebase Cloud Storage
* [x] Restaurant Screen consists of two fragments:
  * [x] Fragment 1 displays images of the restaurant/food with horizontal scrolling
  * [x] Fragment 2 displays a list of food items with prices, add and delete buttons, and quantity tracking
    * [x] Minimum of 5 food items for each restaurant
    * [x] Clicking on the checkout button navigates the user to the Checkout screen
* [x] Checkout Screen displays all added food items in a RecyclerView
  * [x] Each item includes name, price, quantity, and the name of the restaurant
  * [x] Add and Subtract buttons for each item to modify quantity
  * [x] Swiping left deletes the food item from the order
  * [x] If a food item is added twice, display it once with quantity 2 and adjusted price
  * [x] Includes an EditText for entering delivery address
  * [x] Includes a TextBox for entering special instructions
  * [x] Includes a Modify Order button to go back to the Restaurant screen and modify the order
  * [x] Includes a Place Order button that:
    * [x] Saves order details to the order database
    * [x] Calculates delivery time using a specified formula
    * [x] Attaches a background service to check delivery time and generate a notification upon completion
    * [x] Navigates the user to the Orders screen
* [x] Orders Screen displays details of past orders fetched from the database
  * [x] Each order includes name, price, date and time, quantity, restaurant name, and delivery address
  * [x] Includes a Track Order button to navigate the user to the Map screen
* [x] Map Screen displays the location of a destination on the map with a marker
  * [x] Calculates the distance to the destination and highlights the route on the map
* [x] Recent Orders Screen displays all orders placed in the past, sorted by date
  * [x] Each order includes a "Place this order again" button to save the order again to the database
  * [x] Reflects in recent restaurants on the Home screen
  * [x] Takes the user to the order screen to track the order
* [x] Calendar View Screen displays a calendar view with all days in a month
  * [x] Clicking on a day highlights it and displays the amount spent on that day


The following **extensions** are implemented:

## Extra Credits (25 pts):

* [x] Modify the calendar view fragment to have two tabs (Monthly View and Weekly View)
  * [x] Monthly View shows the previous layout implemented as part of the requirements
  * [x] Monthly View includes images/icons below the date relevant to the displayed message
* [x] Weekly View displays a bar chart graph showing the data of the current week's money spent
  * [x] Height of bars depends on the money spent on that date
  * [x] Bars are clickable and show a pop-up window with the description of total money spent and the name of the restaurant from which the order was placed

## Video Walkthrough

Here's a walkthrough of implemented user stories:

<img src='Food-Delivery-Walkthough.gif' title='Food-Delivery-Walkthough.gif' width='50%' alt='Food-Delivery-Walkthough.gif' />

GIF created with [LiceCap](http://www.cockos.com/licecap/).

## Notes

1. Everything went smoothly but the map

## License

    Copyright [2023] [Marvellinus Vincent]

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
