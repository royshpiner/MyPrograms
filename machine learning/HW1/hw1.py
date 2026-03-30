###### Your ID ######
# ID1: 207175746
# ID2: 323832840
#####################

# imports 
import numpy as np
import pandas as pd

def preprocess(X,y):
    """
    Perform Standardization on the features and true labels.

    Input:
    - X: Input data (m instances over n features).
    - y: True labels (m instances).

    Returns:
    - X: The Standardized input data.
    - y: The Standardized true labels.
    """
    ###########################################################################
    # TODO: Implement the normalization function.                             #
    ###########################################################################
    X = (X - X.mean(axis=0)) / X.std(axis=0)  #standartization of x values
    y = (y - y.mean(axis=0)) / y.std(axis=0)  #standartization of y values
    ###########################################################################
    #                             END OF YOUR CODE                            #
    ###########################################################################
    return X, y

def apply_bias_trick(X):
    """
    Applies the bias trick to the input data.

    Input:
    - X: Input data (n instances over p features).

    Returns:
    - X: Input data with an additional column of ones in the
        zeroth position (n instances over p+1).
    """
    ###########################################################################
    # TODO: Implement the bias trick by adding a column of ones to the data.  #
    ###########################################################################
    X = np.c_[np.ones((X.shape[0])), X]   #add a column of 1's
    ###########################################################################
    #                             END OF YOUR CODE                            #
    ###########################################################################
    return X

def compute_loss(X, y, theta):
    """
    Computes the average squared difference between an observation's actual and
    predicted values for linear regression.  

    Input:
    - X: Input data (n instances over p features).
    - y: True labels (n instances).
    - theta: the parameters (weights) of the model being learned.

    Returns:
    - J: the loss associated with the current set of parameters (single number).
    """
    
    J = 0  # We use J for the loss.
    ###########################################################################
    # TODO: Implement the MSE loss function.                                  #
    ###########################################################################
    a = (X @ theta) - y # @ = np.dot
    J = np.mean(a ** 2) / 2  # Compute MSE with scaling factor
    ###########################################################################
    #                             END OF YOUR CODE                            #
    ###########################################################################
    return J

def gradient_descent(X, y, theta, eta, num_iters):
    """
    Learn the parameters of the model using gradient descent using 
    the training set. Gradient descent is an optimization algorithm 
    used to minimize some (loss) function by iteratively moving in 
    the direction of steepest descent as defined by the negative of 
    the gradient. We use gradient descent to update the parameters
    (weights) of our model.

    Input:
    - X: Input data (n instances over p features).
    - y: True labels (n instances).
    - theta: The parameters (weights) of the model being learned.
    - eta: The learning rate of your model.
    - num_iters: The number of updates performed.

    Returns:
    - theta: The learned parameters of your model.
    - J_history: the loss value for every iteration.
    """
    
    theta = theta.copy() # optional: theta outside the function will not change
    J_history = [] # Use a python list to save the loss value in every iteration
    ###########################################################################
    # TODO: Implement the gradient descent optimization algorithm.            #
    ###########################################################################
    n = X.shape[0] # number of instances
    for i in range(num_iters):
        J_history.append(compute_loss(X, y, theta))
        a = (X @ theta) - y
        theta -= (eta / n) * (X.T @ a) # vector update
    ###########################################################################
    #                             END OF YOUR CODE                            #
    ###########################################################################
    return theta, J_history

def compute_pinv(X, y):
    """
    Compute the optimal values of the parameters using the pseudoinverse
    approach as you saw in class using the training set.

    #########################################
    #### Note: DO NOT USE np.linalg.pinv ####
    #########################################

    Input:
    - X: Input data (n instances over p features).
    - y: True labels (n instances).

    Returns:
    - pinv_theta: The optimal parameters of your model.
    """
    
    pinv_theta = []
    ###########################################################################
    # TODO: Implement the pseudoinverse algorithm.                            #
    ###########################################################################
    inverse = np.linalg.inv(X.T @ X) #inverse of x tranpose multiplied by x
    pinv_theta = inverse @ X.T @ y   # multiply the iversed by the transpose and by vector y for calculating theta
    ###########################################################################
    #                             END OF YOUR CODE                            #
    ###########################################################################
    return pinv_theta

def gradient_descent_stop_condition(X, y, theta, eta, max_iter, epsilon=1e-8):
    """
    Learn the parameters of your model using the training set, but stop 
    the learning process once the improvement of the loss value is smaller 
    than epsilon. This function is very similar to the gradient descent 
    function you already implemented.

    Input:
    - X: Input data (n instances over p features).
    - y: True labels (n instances).
    - theta: The parameters (weights) of the model being learned.
    - eta: The learning rate of your model.
    - max_iter: The maximum number of iterations.
    - epsilon: The threshold for the improvement of the loss value.
    Returns:
    - theta: The learned parameters of your model.
    - J_history: the loss value for every iteration.
    """
    
    theta = theta.copy() # optional: theta outside the function will not change
    J_history = [] # Use a python list to save the loss value in every iteration
    ###########################################################################
    # TODO: Implement the gradient descent with stop condition optimization algorithm.  #
    ###########################################################################
    n = X.shape[0] # number of instances
    J_history.append(compute_loss(X, y, theta))
    for i in range(1, max_iter + 1):
        a = (X @ theta) - y
        theta -= (eta / n) * (X.T @ a) # vector update
        loss = compute_loss(X, y, theta)
        if np.isinf(loss): break # in order to avoid an exception when calculating the difference
        J_history.append(loss)
        if abs(J_history[i-1] - J_history[i]) < epsilon: break # check if loss difference is smaller then epsilon
               
    ###########################################################################
    #                             END OF YOUR CODE                            #
    ###########################################################################
    return theta, J_history

def find_best_learning_rate(X_train, y_train, X_val, y_val, iterations):
    """
    Iterate over the provided values of eta and train a model using 
    the training dataset. Maintain a python dictionary with eta as the 
    key and the loss on the validation set as the value.

    You should use the efficient version of gradient descent for this part. 

    Input:
    - X_train, y_train, X_val, y_val: the training and validation data
    - iterations: maximum number of iterations

    Returns:
    - eta_dict: A python dictionary - {eta_value : validation_loss}
    """
    
    etas = [0.00001, 0.00003, 0.0001, 0.0003, 0.001, 0.003, 0.01, 0.03, 0.1, 0.3, 1, 2, 3]
    eta_dict = {} # {eta_value: validation_loss}
    ###########################################################################
    # TODO: Implement the function and find the best eta value.             #
    ###########################################################################
    np.random.seed(42)
    shape = X_train.shape[1]
    i_theta = np.random.random(shape)  #get a random theta
    for eta in etas:
        theta, _ = gradient_descent_stop_condition(X_train ,y_train, i_theta, eta, iterations)  # calculate the final theta
        eta_dict[eta] = compute_loss(X_val, y_val, theta) # update the dictionary for current eta

    ###########################################################################
    #                             END OF YOUR CODE                            #
    ###########################################################################
    return eta_dict

def forward_feature_selection(X_train, y_train, X_val, y_val, best_eta, iterations):
    """
    Forward feature selection is a greedy, iterative algorithm used to 
    select the most relevant features for a predictive model. The objective 
    of this algorithm is to improve the model's performance by identifying 
    and using only the most relevant features, potentially reducing overfitting, 
    improving accuracy, and reducing computational cost.

    You should use the efficient version of gradient descent for this part. 

    Input:
    - X_train, y_train, X_val, y_val: the input data without bias trick
    - best_eta: the best learning rate previously obtained
    - iterations: maximum number of iterations for gradient descent

    Returns:
    - selected_features: A list of selected top 5 feature indices
    """
    selected_features = []
    #####c######################################################################
    # TODO: Implement the function and find the best eta value.             #
    ###########################################################################
    while len(selected_features) < 5:
        feature_dict = {} # {fiture_index: validation_loss}
        np.random.seed(42)
        shape = len(selected_features) + 2
        i_theta = np.random.random(shape)
        for i in range(X_train.shape[1]):   #run on all columns
            if i not in selected_features:   #if feature isn't selected already
                train = apply_bias_trick(X_train[:, selected_features])
                val = apply_bias_trick(X_val[:, selected_features])
                train = np.column_stack((train, X_train[:, i]))
                val = np.column_stack((val, X_val[:, i]))
                theta, _ = gradient_descent_stop_condition(train ,y_train, i_theta, best_eta, iterations)
                feature_dict[i] = compute_loss(val, y_val, theta)
        best_feature = min(feature_dict, key=feature_dict.get) #find best feature by minimum loss
        selected_features.append(best_feature)      #add current best feature to selected features
    ###########################################################################
    #                             END OF YOUR CODE                            #
    ###########################################################################
    return selected_features

def create_square_features(df):
    """
    Create square features for the input data.

    Input:
    - df: Input data (n instances over p features) as a dataframe.

    Returns:
    - df_poly: The input data with polynomial features added as a dataframe
               with appropriate feature names
    """

    df_poly = df.copy()
    ###########################################################################
    # TODO: Implement the function to add polynomial features                 #
    ###########################################################################
    new_features = {}
    for i, feature1 in enumerate(df.columns):
        new_features[f"{feature1}^2"] = df[feature1] ** 2   # Add square features
        for j, feature2 in enumerate(df.columns):
            if j > i:  # Avoid duplicate interaction terms
                name = f"{feature1}*{feature2}"
                new_features[name] = df[feature1] * df[feature2]
    df_new = pd.DataFrame(new_features, index=df.index)    # Convert to DataFrame and concatenate all at once
    df_poly = pd.concat([df, df_new], axis=1)
    ###########################################################################
    #                             END OF YOUR CODE                            #
    ###########################################################################
    return df_poly