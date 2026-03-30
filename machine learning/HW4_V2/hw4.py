import numpy as np

def add_bias_term(X):
    """
    Add a bias term to each sample of the input data.
    """

    ###########################################################################
    # TODO: Implement the function in section below.                          #
    ###########################################################################
    X = np.c_[np.ones((X.shape[0])), X]   #add a column of 1's
    ###########################################################################
    #                             END OF YOUR CODE                            #
    ###########################################################################
    return X

class LogisticRegressionGD():
    """
    Logistic Regression Classifier.

    Fields:
    -------
    w_ : array-like, shape = [n_features]
      Weights vector, where n_features is the number of features.
    eta : float
      Learning rate (between 0.0 and 1.0)
    max_iter : int
      Maximum number of iterations for gradient descent
    eps : float
      minimum change in the BCE loss to declare convergence
    random_state : int
      Random number generator seed for random weight
      initialization.
    """
    
    def __init__(self, learning_rate=0.0001, max_iter=10000, eps=0.000001, random_state=1):
       
        # Initialize the weights vector with small random values
        self.random_state = random_state
        self.w_ = np.nan
        self.learning_rate = learning_rate
        self.max_iter = max_iter
        self.eps = eps
        self.class_names = None


    def predict_proba(self, X):
        """
        Return the predicted probabilities of the instances for the positive class (class 1)

        Parameters
        ----------
        X : {array-like}, shape = [n_samples, n_features]
          Instance vectors, where n_samples is the number of samples and
          n_features is the number of features.

        Returns
        -------
        y_pred_prob : array-like, shape = [n_examples]
          Predicted probabilities (for class 1) for all the instances
        """
        class_1_prob = np.nan * np.ones(X.shape[0])

        ###########################################################################
        # TODO: Implement the function in section below.                          #
        ###########################################################################
        class_1_prob = 1 / (1 + np.exp(-(X @ self.w_)))
        ###########################################################################
        #                             END OF YOUR CODE                            #
        ###########################################################################
        return class_1_prob
        

    def predict(self, X, threshold=0.5):
        """
        Return the predicted class label according to the threshold

        Parameters
        ----------
        X : {array-like}, shape = [n_samples, n_features]
          Instance vectors, where n_samples is the number of samples and
          n_features is the number of features.
        threshold : float, optional
          Threshold for the predicted class label.
          Predict class 1 if the probability is greater than or equal to the threshold and 0 otherwise.
          Default is 0.5. 
        """
        y_pred = np.nan * np.ones(X.shape[0])
    
        ###########################################################################
        # TODO: Implement the function in section below.                          #
        ###########################################################################
        y_pred = np.where(self.predict_proba(X) >= threshold, self.class_names[1], self.class_names[0])
        ###########################################################################
        #                             END OF YOUR CODE                            #
        ###########################################################################
        return y_pred

    
    def BCE_loss(self, X, y):
        """
        Calculate the BCE loss (not needed for training)

        Parameters
        ----------
        X : {array-like}, shape = [n_samples, n_features]
          Instance vectors, where n_samples is the number of samples and
          n_features is the number of features.
        y : array-like, shape = [n_samples]
          Class labels. 

        Returns
        -------
        BCE_loss : float
          The BCE loss.
          Make sure to normalize the BCE loss by the number of samples.
        """

        y_01 = np.where(y == self.class_names[0], 0, 1) # represents the class 0/1 labels
        loss = None
        ###########################################################################
        # TODO: Implement the function in section below.                          #
        ###########################################################################
        y_pred_prob = self.predict_proba(X)
        # Calculate the BCE loss
        loss = -np.mean(y_01 * np.log2(y_pred_prob + 1e-6) + (1 - y_01) * np.log2(1 - y_pred_prob + 1e-6))
        # Add a small constant to avoid log(0)
        ###########################################################################
        #                             END OF YOUR CODE                            #
        ###########################################################################
        return loss


    def fit(self, X, y):
        """ 
        Fit training data by minimizing the BCE loss using gradient descent.
        Updates the weight vector (field of the object) in each iteration using gradient descent.
        The gradient should correspond to the BCE loss normalized by the number of samples.
        Stop the function when the difference between the previous BCE loss and the current is less than eps
        or when you reach max_iter.
        Collect the BCE loss in each iteration in the loss variable.

        Parameters
        ----------
        X : {array-like}, shape = [n_samples, n_features]
          Training vectors, where n_samples is the number of samples and
          n_features is the number of features.
        y : array-like, shape = [n_samples]
          Class labels.

        """

        # Initial weights are set in constructor
        # Initialize the cost history
        loss = []

        # make sure to use 0/1 labels:
        self.class_names = np.unique(y)
        y_01 = np.where(y == self.class_names[0], 0, 1)
        np.random.seed(self.random_state)
        self.w_ = 1e-6 * np.random.randn(X.shape[1])

        ###########################################################################
        # TODO: Implement the function in section below.                          #
        ###########################################################################
        for i in range(self.max_iter):
            # Calculate the predicted probabilities
            y_pred_prob = self.predict_proba(X)

            # Calculate the BCE loss and append it to the loss history
            loss.append(self.BCE_loss(X, y))
           
            if i > 0 and abs(loss[-1] - loss[-2]) < self.eps:  # Check for convergence
                break

            self.w_ -= self.learning_rate * np.dot(X.T, (y_pred_prob - y_01)) / X.shape[0]  # Update the weights using gradient descent
        ###########################################################################
        #                             END OF YOUR CODE                            #
        ###########################################################################
        

def select_learning_rate(X_train, y_train, learning_rates, max_iter):
    """
    Select the learning rate attaining the minimal BCE after max_iter GD iterations

    Parameters
    ----------
    X_train : {array-like}, shape = [n_samples, n_features]
      Training vectors, where n_samples is the number of samples and
      n_features is the number of features.
    y_train : array-like, shape = [n_samples]
      Class labels.
    learning_rates : list
      The list of learning rates to test.
    max_iter : int
      The maximum number of iterations for the gradient descent.

    Returns
    -------
    selected_learning_rate : float
      The learning rate attaining the minimal BCE after max_iter GD iterations.
    """
    # Initialize variables to keep track of the minimum BCE and the corresponding learning rate
    min_bce = float('inf')
    selected_learning_rate = None
    
    ###########################################################################
    # TODO: Implement the function in section below.                          #
    ###########################################################################
    for lr in learning_rates:
        # Create a new LogisticRegressionGD instance with the current learning rate
        model = LogisticRegressionGD(learning_rate=lr, max_iter=max_iter)
        
        # Fit the model to the training data
        model.fit(X_train, y_train)
        
        # Calculate the BCE loss on the training data
        bce_loss = model.BCE_loss(X_train, y_train)
        
        # Check if this is the minimum BCE loss found so far
        if bce_loss < min_bce:
            min_bce = bce_loss
            selected_learning_rate = lr
    ###########################################################################
    #                             END OF YOUR CODE                            #
    ###########################################################################
    return selected_learning_rate


def cv_accuracy_and_bce_error(X, y, n_folds):
    """
    Calculate the accuracy and BCE error of the model using cross-validation.

    Parameters
    ----------
    X : {array-like}, shape = [n_samples, n_features]
      Training samples, where n_samples is the number of samples and
      n_features is the number of features.
    y : array-like, shape = [n_samples]
      Target values.
    n_folds : int
      The number of folds for cross-validation.
    Returns 
    -------
    The function returns two lists: accuracies and BCE_losses.
    Each list contains the results for each of the n_folds of the cross-validation.
    """

    # Split the data into n_folds and initialize the lists for accuracies and BCE losses
    X_splits = np.array_split(X, n_folds)
    y_splits = np.array_split(y, n_folds)
    accuracies = []
    BCE_losses = []

    ###########################################################################
    # TODO: Implement the function in section below.                          #
    ###########################################################################
    for i in range(n_folds):
        # Create training and validation sets
        X_val = X_splits[i]
        y_val = y_splits[i]
        X_train = np.concatenate(X_splits[:i] + X_splits[i+1:])
        y_train = np.concatenate(y_splits[:i] + y_splits[i+1:])
        
        model = LogisticRegressionGD()         # Initialize the model
        model.fit(X_train, y_train)        # Fit the model on the training data
        y_pred = model.predict(X_val)        # Predict on the validation set
        # Calculate accuracy
        accuracy = np.mean(y_pred == y_val)
        accuracies.append(accuracy)
        # Calculate BCE loss
        bce_loss = model.BCE_loss(X_val, y_val)
        BCE_losses.append(bce_loss)
    ###########################################################################
    #                             END OF YOUR CODE                            #
    ###########################################################################

    return accuracies, BCE_losses


def calc_and_print_metrics(y_true, y_pred, positive_class):
    """
    Calculate and print the metrics for the LogisticRegression classifier.
    """
    # Calculate the metrics
    
    tp, fp, tn, fn = None, None, None, None
    tpr, fpr, tnr, fnr = None, None, None, None
    accuracy, precision, recall = None, None, None
    risk = None
    f1 = None

    ###########################################################################
    # TODO: Implement the function in section below.                          #
    ###########################################################################
    tp = np.sum((y_true == positive_class) & (y_pred == positive_class))  # True Positives
    fp = np.sum((y_true != positive_class) & (y_pred == positive_class))  # False Positives
    tn = np.sum((y_true != positive_class) & (y_pred != positive_class))  # True Negatives
    fn = np.sum((y_true == positive_class) & (y_pred != positive_class))  # False Negatives
    tpr = tp / (tp + fn) if (tp + fn) > 0 else 0  # True Positive Rate
    fpr = fp / (fp + tn) if (fp + tn) > 0 else 0  # False Positive Rate
    tnr = tn / (tn + fp) if (tn + fp) > 0 else 0  # True Negative Rate
    fnr = fn / (fn + tp) if (fn + tp) > 0 else 0  # False Negative Rate
    accuracy = (tp + tn) / (tp + fp + tn + fn) if (tp + fp + tn + fn) > 0 else 0  # Accuracy
    precision = tp / (tp + fp) if (tp + fp) > 0 else 0  # Precision
    recall = tpr  # Recall is the same as True Positive Rate
    risk = (fp + fn) / (tp + fp + tn + fn) if (tp + fp + tn + fn) > 0 else 0  # Risk
    f1 = 2 * (precision * recall) / (precision + recall) if (precision + recall) > 0 else 0  # F1 Score
    ###########################################################################
    #                             END OF YOUR CODE                            #
    ###########################################################################
    # Print the metrics    
    print(f"#TP: {tp}, #FP: {fp}, #TN: {tn}, #FN: {fn}")
    print(f"#TPR: {tpr}, #FPR: {fpr}, #TNR: {tnr}, #FNR: {fnr}")
    print(f"Accuracy: {accuracy}, Risk: {risk}, Precision: {precision}, Recall: {recall}")
    print(f"F1: {f1}")



def fpr_tpr_per_threshold(y_true, positive_class_probs, positive_class="9"):
    """
    Calculate FPR and TPR of a given classifier for different thresholds

    Parameters
    ----------
    y_true : array-like, shape = [n]
      True class labels for the n samples
    positive_class_probs : array-like, shape = [n]
      Predicted probabilities for the positive class for the n samples
    positive_class : str, optional
      The label of the class to be considered as the positive class
    """
    fpr = []
    tpr = []
    # consider thresholds from 0 to 1 with step 0.01
    prob_thresholds = np.arange(0, 1, 0.01)
    y_true_binary = np.where(y_true == positive_class, 1, 0)
    
    ###########################################################################
    # TODO: Implement the function in section below.                          #
    ###########################################################################
    for threshold in prob_thresholds:
        # Predict class labels based on the threshold
        negative_class = [c for c in np.unique(y_true) if c != positive_class][0]
        y_pred = np.where(positive_class_probs >= threshold, positive_class, negative_class)
        # Calculate True Positives, False Positives, True Negatives, False Negatives
        tp = np.sum((y_true_binary == 1) & (y_pred == positive_class))
        fp = np.sum((y_true_binary == 0) & (y_pred == positive_class))
        tn = np.sum((y_true_binary == 0) & (y_pred != positive_class))
        fn = np.sum((y_true_binary == 1) & (y_pred != positive_class))
        
        # Calculate TPR and FPR
        tpr.append(tp / (tp + fn) if (tp + fn) > 0 else 0)
        fpr.append(fp / (fp + tn) if (fp + tn) > 0 else 0)
    ###########################################################################
    #                             END OF YOUR CODE                            #
    ###########################################################################
    return fpr, tpr



