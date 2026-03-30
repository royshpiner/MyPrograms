import yaml
from errors import *
from item import Item
from shopping_cart import ShoppingCart

class Store:
    def __init__(self, path):
        with open(path) as inventory:
            items_raw = yaml.load(inventory, Loader=yaml.FullLoader)['items']
        self._items = self._convert_to_item_objects(items_raw)
        self._shopping_cart = ShoppingCart()

    @staticmethod
    def _convert_to_item_objects(items_raw):
        return [Item(item['name'],
                     int(item['price']),
                     item['hashtags'],
                     item['description'])
                for item in items_raw]

    def get_items(self) -> list:
        return self._items

    def search_by_name(self, item_name: str) -> list:
        """
        searches for an item in the store by it's name
        recieves a string of an item name
        returns a list conatining all the matching names which is sorted by number of hashtags and then by name
        """
        cart_tags = [tag for item in self._shopping_cart.items for tag in item.hashtags] #create a tag for all items in the shopping cart
        prod_contain_name = []
        for product in self._items:      #run on all items in store by name without cart and add them to list if has the name
            if item_name in product.name and product not in self._shopping_cart.items:
                prod_contain_name.append(product)  #add the profuct to the list
        prod_contain_name.sort(key=lambda x: (-sum([cart_tags.count(tag) for tag in x.hashtags]),x.name)) # Sort the products by the number of hashtags with the shopping cart in descending order, and then by name if tie 
        return prod_contain_name
    
    def search_by_hashtag(self, hashtag: str) -> list:
        """
        searches for an item in the store by it's hashtag
        recieves a string of an item hashtag
        returns a list conatining all the matching hashtags which is sorted by number of hashtags and then by name
        """
        cart_tags = [tag for item in self._shopping_cart.items for tag in item.hashtags]#create a tag for all items in the shopping cart
        prod_with_hashtag = []
        for product in self._items:       #run on all items in store by hashtag without cart and add them to list if has the hashtag
            if hashtag in product.hashtags and product not in self._shopping_cart.items:
                prod_with_hashtag.append(product)  #add the profuct to the list
    
        prod_with_hashtag.sort(key=lambda x: (-sum([cart_tags.count(tag) for tag in x.hashtags]),x.name)) # Sort the products by the number of hashtags with the shopping cart in descending order, and then by name if tie 
        return prod_with_hashtag
    
    def add_item(self, item_name: str):
        """
        adds and the item recieved to the shopping cart
        recieves the name of the item
        throws an error if the item isn't in the stor or there are more than 1 items conatining the name
        
        """
        names = [item for item in self._items if item_name in item.name] #list all items with the name(including in cart) 
        if not names:         #check if we have it if not throw error
            raise ItemNotExistError()
        if len(names) > 1:      #if there are more than 1 mathces er throw an error
            raise TooManyMatchesError()
        #if item is in the cart the shoppingcart class will throw the error
        item_to_add = names[0]     #if only 1 item, add it to cart
        self._shopping_cart.add_item(item_to_add)
        
    def remove_item(self, item_name: str):
        """
        removes the item recieved to the shopping cart
        recieves the name of the item
        throws an error if the item isn't in the store or there are more than 1 items conatining the name
        """
        names = [item for item in self._items if item_name in item.name]  #list all items with the name(including in cart)
        if not names:    #check if we have it if not throw error
            raise ItemNotExistError()
        if len(names) > 1:     #if there are more than 1 mathces er throw an error
            raise TooManyMatchesError()
        item_to_remove = names[0]     #if only 1 item, remove from our cart
        self._shopping_cart.remove_item(item_to_remove.name)

    def checkout(self) -> int:
        """
        calculate shopping cart price
        returns an int of the price of the cart
        """
        price = self._shopping_cart.get_subtotal()   #calculate cart price
        return price
