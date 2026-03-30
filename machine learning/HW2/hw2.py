import numpy as np
import matplotlib.pyplot as plt

### Chi square table values ###
# The first key is the degree of freedom 
# The second key is the p-value cut-off
# The values are the chi-statistic that you need to use in the pruning

chi_table = {1: {0.5 : 0.45,
             0.25 : 1.32,
             0.1 : 2.71,
             0.05 : 3.84,
             0.0001 : 100000},
         2: {0.5 : 1.39,
             0.25 : 2.77,
             0.1 : 4.60,
             0.05 : 5.99,
             0.0001 : 100000},
         3: {0.5 : 2.37,
             0.25 : 4.11,
             0.1 : 6.25,
             0.05 : 7.82,
             0.0001 : 100000},
         4: {0.5 : 3.36,
             0.25 : 5.38,
             0.1 : 7.78,
             0.05 : 9.49,
             0.0001 : 100000},
         5: {0.5 : 4.35,
             0.25 : 6.63,
             0.1 : 9.24,
             0.05 : 11.07,
             0.0001 : 100000},
         6: {0.5 : 5.35,
             0.25 : 7.84,
             0.1 : 10.64,
             0.05 : 12.59,
             0.0001 : 100000},
         7: {0.5 : 6.35,
             0.25 : 9.04,
             0.1 : 12.01,
             0.05 : 14.07,
             0.0001 : 100000},
         8: {0.5 : 7.34,
             0.25 : 10.22,
             0.1 : 13.36,
             0.05 : 15.51,
             0.0001 : 100000},
         9: {0.5 : 8.34,
             0.25 : 11.39,
             0.1 : 14.68,
             0.05 : 16.92,
             0.0001 : 100000},
         10: {0.5 : 9.34,
              0.25 : 12.55,
              0.1 : 15.99,
              0.05 : 18.31,
              0.0001 : 100000},
         11: {0.5 : 10.34,
              0.25 : 13.7,
              0.1 : 17.27,
              0.05 : 19.68,
              0.0001 : 100000}}

def calc_gini(data):
    """
    Calculate gini impurity measure of a dataset.
 
    Input:
    - data: any dataset where the last column holds the labels.
 
    Returns:
    - gini: The gini impurity value.
    """
    gini = 0.0
    ###########################################################################
    # TODO: Implement the function.                                           #
    ###########################################################################
    labels = data[:, -1]
    _, counts = np.unique(labels, return_counts=True)
    probabilities = counts / counts.sum()
    gini = 1 - np.sum(probabilities ** 2)
    ###########################################################################
    #                             END OF YOUR CODE                            #
    ###########################################################################
    return gini

def calc_entropy(data):
    """
    Calculate the entropy of a dataset.

    Input:
    - data: any dataset where the last column holds the labels.

    Returns:
    - entropy: The entropy value.
    """
    entropy = 0.0
    ###########################################################################
    # TODO: Implement the function.                                           #
    ###########################################################################
    labels = data[:, -1] 
    _, counts = np.unique(labels, return_counts=True)    
    probabilities = counts / counts.sum()
    log_prob = np.log2(probabilities)
    entropy = -sum(probabilities *log_prob)
    ###########################################################################
    #                             END OF YOUR CODE                            #
    ###########################################################################
    return entropy

class DecisionNode:

    
    def __init__(self, data, impurity_func, feature=-1,depth=0, chi=1, max_depth=1000, gain_ratio=False):
        
        self.data = data # the data instances associated with the node
        self.terminal = False # True iff node is a leaf
        self.feature = feature # column index of feature/attribute used for splitting the node
        self.pred = self.calc_node_pred() # the class prediction associated with the node
        self.depth = depth # the depth of the node
        self.children = [] # the children of the node (array of DecisionNode objects)
        self.children_values = [] # the value associated with each child for the feature used for splitting the node
        self.max_depth = max_depth # the maximum allowed depth of the tree
        self.chi = chi # the P-value cutoff used for chi square pruning
        self.impurity_func = impurity_func # the impurity function to use for measuring goodness of a split
        self.gain_ratio = gain_ratio # True iff GainRatio is used to score features
        self.feature_importance = 0
    
    def calc_node_pred(self):
        """
        Calculate the node's prediction.

        Returns:
        - pred: the prediction of the node
        """
        pred = None
        ###########################################################################
        # TODO: Implement the function.                                           #
        ###########################################################################
        labels, counts = np.unique(self.data[:, -1], return_counts=True)  #get the number of occurences of eace class
        pred = labels[np.argmax(counts)]# return the class with highest amount of occurances as the pred
        ###########################################################################
        #                             END OF YOUR CODE                            #
        ###########################################################################
        return pred
        
    def add_child(self, node, val):
        """
        Adds a child node to self.children and updates self.children_values

        This function has no return value
        """
        ###########################################################################
        # TODO: Implement the function.                                           #
        ###########################################################################
        self.children.append(node)   
        self.children_values.append(val)
        ###########################################################################
        #                             END OF YOUR CODE                            #
        ###########################################################################
    
    def goodness_of_split(self, feature):
        """
        Calculate the goodness of split of a dataset given a feature and impurity function.

        Input:
        - feature: the feature index the split is being evaluated according to.

        Returns:
        - goodness: the goodness of split
        - groups: a dictionary holding the data after splitting 
                  according to the feature values.
        """
        goodness = 0
        groups = {} # groups[feature_value] = data_subset
        ###########################################################################
        # TODO: Implement the function.                                           #
        ###########################################################################
        values, counts = np.unique(self.data[:, feature], return_counts=True)

        for val in values:
            groups[val] = self.data[self.data[:, feature] == val]

        total = self.data.shape[0]
        sigma = 0

        if self.gain_ratio:
            si = 0
            for i, val in enumerate(values):
                proportion = counts[i] / total
                sigma += proportion * calc_entropy(groups[val])
                si -= proportion * np.log2(proportion)       #si = split info
            ig = calc_entropy(self.data) - sigma             #ig = information gain
            goodness = ig / si if si > 0 else 0
        else:
            for i, val in enumerate(values):
                proportion = counts[i] / total
                sigma += proportion * self.impurity_func(groups[val])
            goodness = self.impurity_func(self.data) - sigma
        ###########################################################################
        #                             END OF YOUR CODE                            #
        ###########################################################################
        return goodness, groups
        
    def calc_feature_importance(self, n_total_sample):
        """
        Calculate the selected feature importance.
        
        Input:
        - n_total_sample: the number of samples in the dataset.

        This function has no return value - it stores the feature importance in 
        self.feature_importance
        """
        ###########################################################################
        # TODO: Implement the function.                                           #
        ###########################################################################
        
        goodness, _ = self.goodness_of_split(self.feature)  # Compute goodness of split for the selected feature
        relative_weight = self.data.shape[0] / n_total_sample  # compute feature weight
        self.feature_importance = relative_weight * goodness   # calculate the importance of the feature
        ###########################################################################
        #                             END OF YOUR CODE                            #
        ###########################################################################
    
    def split(self):
        """
        Splits the current node according to the self.impurity_func. This function finds
        the best feature to split according to and create the corresponding children.
        This function should support pruning according to self.chi and self.max_depth.

        This function has no return value
        """
        ###########################################################################
        # TODO: Implement the function.                                           #
        ###########################################################################
        if np.all(self.data[:, -1] == self.data[0, -1]): #if pure label then don't split
            self.terminal = True
            return
        
        if self.depth >= self.max_depth or self.terminal: #if node is a leaf or at the max depth
            return

        best_goodness = -1
        best_feature = -1
        best_groups = {}

        for feature in range(self.data.shape[1] - 1):   #run on all the attributes and find the best split
            goodness, groups = self.goodness_of_split(feature)
            if goodness > best_goodness:
                best_goodness = goodness
                best_feature = feature
                best_groups = groups

        if best_goodness == 0 or best_feature == -1: #if no good split, than current node is a leaf
            self.terminal = True
            return

        self.feature = best_feature
        self.goodness = best_goodness
        if len(best_groups) <= 1:
            self.terminal = True
            return
        if self.chi != 1:
            chi_val = self.calc_chi(best_groups)  #calculaet the chi of the current best split
            deg_of_freedom = len(best_groups) - 1
            chi_val_from_table = chi_table[deg_of_freedom][self.chi]
            if chi_val < chi_val_from_table:   #if it isn't worth the split by th p value, then make it a leaf
                self.terminal = True
                return


        for val, subset in best_groups.items():  #create a child node for each value of best feature
            child = DecisionNode(data=subset,impurity_func=self.impurity_func,feature=-1,depth=self.depth + 1,chi=self.chi,max_depth=self.max_depth,gain_ratio=self.gain_ratio)
            self.add_child(child, val)
        
        ###########################################################################
        #                             END OF YOUR CODE                            #
        ###########################################################################

    def calc_chi(self, subdata):
        chi_square = 0
        labels = self.data[:, -1]
        size = len(labels)
        final_label_count = {label: np.sum(labels == label) for label in np.unique(labels)}
        for feature_val, sub in subdata.items():
            sub_size = len(sub)
            labels = sub[:, -1]
            sub_label_count = {label: np.sum(labels == label) for label in np.unique(labels)}
            for label, count in final_label_count.items():

                expected = sub_size * count / size    # Calculate the parameters in the formula.
                observed = sub_label_count.get(label, 0)
                chi_square += (observed-expected)**2 / expected    # Calculate the chi square according to the formula.
        return chi_square

                    
class DecisionTree:
    def __init__(self, data, impurity_func, feature=-1, chi=1, max_depth=1000, gain_ratio=False, depth=0):
        self.data = data # the training data used to construct the tree
        self.root = None # the root node of the tree
        self.max_depth = max_depth # the maximum allowed depth of the tree
        self.chi = chi # the P-value cutoff used for chi square pruning
        self.impurity_func = impurity_func # the impurity function to be used in the tree
        self.gain_ratio = gain_ratio #
        self.depth = depth # the depth was not given in the original code but we added it for a more efficient implementation 

    def depth(self):
        return self.root.depth

    def build_tree(self):
        """
        Build a tree using the given impurity measure and training dataset. 
        You are required to fully grow the tree until all leaves are pure 
        or the goodness of split is 0.

        This function has no return value
        """
        self.root = None
        ###########################################################################
        # TODO: Implement the function.                                           #
        ###########################################################################
        self.root = DecisionNode(data=self.data, impurity_func=self.impurity_func, feature=-1, depth=0, chi=self.chi, max_depth=self.max_depth, gain_ratio=self.gain_ratio) #create a node for the root
        self._build_recursive(self.root)#we added an extra function so the tree will be built recursively
        ###########################################################################
        #                             END OF YOUR CODE                            #
        ###########################################################################


    def _build_recursive(self, node):   #help function
        """
        Recursively builds the tree starting from the given node.
        """
        if node.depth > self.depth:
            self.depth = node.depth
        if node.terminal:
            return

        # Try to split the node
        node.split()
        node.calc_feature_importance(self.data.shape[0])
        # If after split, node is still not terminal, recurse on children
        for child in node.children:
            if child:
                self._build_recursive(child)



    def predict(self, instance):
        """
        Predict a given instance
     
        Input:
        - instance: an row vector from the dataset. Note that the last element 
                    of this vector is the label of the instance.
     
        Output: the prediction of the instance.
        """
        pred = None
        ###########################################################################
        # TODO: Implement the function.                                           #
        ###########################################################################
        node = self.root
        found_child = True

        while not node.terminal and found_child:
            found_child = False
            for child in node.children:
                # Get the expected value from the child’s data
                child_value = child.data[0, node.feature]      # assuming child.data is not empty
                if instance[node.feature] == child_value:      # Compare instance's feature value to this value
                    node = child
                    found_child = True
                    break
        ###########################################################################
        #                             END OF YOUR CODE                            #
        ###########################################################################
        return node.pred

    def calc_accuracy(self, dataset):
        """
        Predict a given dataset 
     
        Input:
        - dataset: the dataset on which the accuracy is evaluated
     
        Output: the accuracy of the decision tree on the given dataset (%).
        """
        accuracy = 0
        ###########################################################################
        # TODO: Implement the function.                                           #
        ###########################################################################
        correct_predictions = 0  # To count the number of correct predictions
        for instance in dataset:
            true_label = instance[-1]  # The true label is the last element in the instance
            predicted_label = self.predict(instance)  # Use the predict method to get the predicted label
            if true_label == predicted_label:  # If prediction is correct, increment the counter
                correct_predictions += 1
    
        accuracy = (correct_predictions / len(dataset)) * 100  # Calculate accuracy as percentage
        ###########################################################################
        #                             END OF YOUR CODE                            #
        ###########################################################################
        return accuracy
        

def depth_pruning(X_train, X_validation):
    """
    Calculate the training and validation accuracies for different depths
    using the best impurity function and the gain_ratio flag you got
    previously. 

    Input:
    - X_train: the training data where the last column holds the labels
    - X_validation: the validation data where the last column holds the labels
 
    Output: the training and validation accuracies per max depth
    """
    training = []
    validation  = []
    root = None
    for max_depth in [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]:
        ###########################################################################
        # TODO: Implement the function.                                           #
        ###########################################################################
        tree = DecisionTree(data=X_train, impurity_func=calc_entropy,  chi=1, max_depth=max_depth, gain_ratio=True)
        tree.build_tree()   #build the tree with the mak depth
        train_acc = tree.calc_accuracy(X_train)  #calc the train accuracy and the val
        val_acc = tree.calc_accuracy(X_validation)
        training.append(train_acc)
        validation.append(val_acc)

        ###########################################################################
        #                             END OF YOUR CODE                            #
        ###########################################################################
    return training, validation


def chi_pruning(X_train, X_test):

    """
    Calculate the training and validation accuracies for different chi values
    using the best impurity function and the gain_ratio flag you got
    previously. 

    Input:
    - X_train: the training data where the last column holds the labels
    - X_validation: the validation data where the last column holds the labels
 
    Output:
    - chi_training_acc: the training accuracy per chi value
    - chi_validation_acc: the validation accuracy per chi value
    - depth: the tree depth for each chi value
    """
    chi_training_acc = []
    chi_validation_acc  = []
    depth = []

    ###########################################################################
    # TODO: Implement the function.                                           #
    ###########################################################################
    p_values = [1, 0.5, 0.25, 0.1, 0.05, 0.0001]
    for p_val in p_values:
        tree = DecisionTree(data=X_train, impurity_func=calc_entropy, gain_ratio=True, chi=p_val)
        tree.build_tree()  #build the tree with the chi as the current p value
        depth.append(tree.depth)
        train_acc = tree.calc_accuracy(X_train)
        val_acc = tree.calc_accuracy(X_test)
        chi_training_acc.append(train_acc)
        chi_validation_acc.append(val_acc)
    ###########################################################################
    #                             END OF YOUR CODE                            #
    ###########################################################################
        
    return chi_training_acc, chi_validation_acc, depth
    


def count_nodes(node):
    """
    Count the number of node in a given tree
 
    Input:
    - node: a node in the decision tree.
 
    Output: the number of node in the tree.
    """
    ###########################################################################
    # TODO: Implement the function.                                           #
    ###########################################################################
    if node.terminal: #if is a leaf
        return 1
    n_nodes = 1  # Count the current node
    for child in node.children:
        n_nodes += count_nodes(child)  # Recursively count children
    ###########################################################################
    #                             END OF YOUR CODE                            #
    ###########################################################################
    return n_nodes
