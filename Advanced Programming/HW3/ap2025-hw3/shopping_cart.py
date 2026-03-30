from errors import *
from item import Item


class ShoppingCart:
    def __init__(self):
        """
        creates an empty shopping cart as a list of items
        """
        self.items: list[Item] = []  #creat an empty list of items
    
    def add_item(self, item: Item):  
        """
        adds the item to our shoppping cart
        recieves the item we wand to add
        raises an error if item already exists in the cart
        """
        if item in self.items:       # if already in our list
            raise ItemAlreadyExistsError()
        self.items.append(item)      #add to list

    def remove_item(self, item_name: str): 
        """
        recieves the item we wand to remove
        removes the item from the shopping cart
        raises an error if item already exists in the cart
        """
        notFound = True
        for curritem in self.items:    #run on all items in the shopping list
            if curritem.name == item_name:   #check if the name we recieved exists in the list
                notFound = False            
                self.items.remove(curritem)  #remove from list if found
        if notFound:  #if it does not exist then we raise an error
            raise ItemNotExistError()

    def get_subtotal(self) -> int:
        """
        returns the price of all items in the shopping cart
        """
        return sum(item.price for item in self.items)  #sum all the prices of items in our shopping list
