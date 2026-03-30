import numpy as np

def get_random_centroids(X, k):
    '''
    Each centroid is a point in RGB space (color) in the image. 
    This function should randomly sample `k` different pixels from the input
    image as the initial centroids for the K-means algorithm.
    The selected `k` pixels should be sampled uniformly from all sets
    of `k` pixels in the image.
    Input: a single image of shape `(num_pixels, 3)` and `k`, the number of centroids. 
    Notice we are flattening the image to a two dimentional array.
    Output: Randomly chosen centroids of shape `(k,3)` as a numpy array. 
    '''
    ###########################################################################
    # TODO: Implement the function.                                           #
    ###########################################################################
    # make sure you return a numpy array
    indexes = np.random.choice(X.shape[0], size=k, replace=False) # selects k random indexes from the flattened image
    centroids = X[indexes]   # selects the pixels corresponding to the random indexes
    return centroids
    ###########################################################################
    #                             END OF YOUR CODE                            #
    ###########################################################################    

def l_p_dist_from_centroids(X, centroids, p=2):
    '''
    Inputs: 
    A single image of shape (num_pixels, 3)
    The centroids of shape (k, 3)
    The parameter p for the L_p norm distance measure.

    Output: numpy array of shape `(k, num_pixels)`,
    in which entry [j,i] holds the distance of the i-th pixel from the j-th centroid.
    '''
    distances = []
    k = len(centroids)
    ###########################################################################
    # TODO: Implement the function.                                           #
    ###########################################################################
    
    for j in range(k):
        # Calculate the L_p distance from each pixel to the j-th centroid
        diff = np.linalg.norm(X - centroids[j], ord=p, axis=1)  # L_p distance for each pixel to the j-th centroid
        distances.append(diff)  # Append the distances for the j-th centroid
    distances = np.array(distances)  # Convert the list of distances to a numpy array                  

    ###########################################################################
    #                             END OF YOUR CODE                            #
    ###########################################################################
    return distances

def kmeans(X, k, p ,max_iter=100, epsilon=1e-8):
    """
    Inputs:
    - X: a single image of shape (num_pixels, 3).
    - k: number of centroids.
    - p: the parameter governing the L_p distance measure.
    - max_iter: the maximum number of iterations to perform.
    - epsilon: the threshold for convergence.

    Outputs:
    - The final centroids as a numpy array.
    - The final assignment of all pixels to the closest centroids as a numpy array.
    - The final WCS as a float.
    """
    cluster_assignments = []
    centroids = get_random_centroids(X, k)
    ###########################################################################
    # TODO: Implement the function.                                           #
    ###########################################################################
    centroids = centroids.astype(np.float32)
    for i in range(max_iter):
        distances = l_p_dist_from_centroids(X, centroids, p)  # L_p distances from all pixels to all centroids
        cluster_assignments = np.argmin(distances, axis=0)  # shape (num_pixels,)
        new_centroids = np.zeros_like(centroids) #calculate new centroids after assigments
        for j in range(k):
            cluster_points = X[cluster_assignments == j]
            if len(cluster_points) > 0:
                new_centroids[j] = np.mean(cluster_points, axis=0)
            else:
                new_centroids[j] = centroids[j]  # O
                
        centrois_changes = np.sqrt(np.sum((new_centroids - centroids) ** 2, axis=1)) 
        max_change = np.max(centrois_changes)  # maximum change in centroids
        if max_change < epsilon:   #check if changes are below epsilon for convergence
            break
        centroids = new_centroids #save new centroids for next iteration
    WCS = 0.0
    for j in range(k):  #compute the spread of cluster
        cluster_points = X[cluster_assignments == j]
        if len(cluster_points) > 0:
            differences = np.abs(cluster_points - centroids[j])
            WCS += np.sum(np.sum(differences ** p, axis=1))  # ||x - μ||^p

    ###########################################################################
    #                             END OF YOUR CODE                            #
    ###########################################################################
    return centroids, cluster_assignments, WCS

def kmeans_pp(X, k, p ,max_iter=100, epsilon=1e-8):
    """
    The kmeans algorithm with alternative centroid initalization.
    Inputs:
    - X: a single image of shape (num_pixels, 3).
    - k: number of centroids.
    - p: the parameter governing the L_p distance measure.
    - max_iter: the maximum number of iterations to perform.
    - epsilon: the threshold for convergence.

    Outputs:
    - The final centroids as a numpy array.
    - The final assignment of all pixels to the closest centroids as a numpy array.
    - The final WCS as a float.
     """
    cluster_assignments = None
    centroids = None
    ###########################################################################
    # TODO: Implement the function.                                           #
    ###########################################################################
    n = X.shape[0]
    centroids = []
    first_centroid = np.random.choice(n)
    centroids.append(X[first_centroid])

    for _ in range(1, k):  #Choose remaining centroids using weighted probability
        current_centroids = np.array(centroids)
        dists = l_p_dist_from_centroids(X, current_centroids, p) #
        min_dists = np.min(dists, axis=0) #distance to the closest centroid for each pixel
        probs = min_dists**2.  
        probs /= np.sum(probs)  # normalize to get probabilities
        next_idx = np.random.choice(n, p=probs)
        centroids.append(X[next_idx])

    centroids = np.array(centroids)

    #Run standard K-means using these centroids (the k means function uses get_random_centroids so we can't use it here)
    cluster_assignments = []
    centroids = centroids.astype(np.float32)
    for i in range(max_iter):
        distances = l_p_dist_from_centroids(X, centroids, p)
        cluster_assignments = np.argmin(distances, axis=0)

        new_centroids = np.zeros_like(centroids)
        for j in range(k):
            cluster_points = X[cluster_assignments == j]
            if len(cluster_points) > 0:
                new_centroids[j] = np.mean(cluster_points, axis=0)
            else:
                new_centroids[j] = centroids[j]

        centrois_changes = np.sqrt(np.sum((new_centroids - centroids) ** 2, axis=1))
        max_change = np.max(centrois_changes)
        if max_change < epsilon:
            break
        centroids = new_centroids

    # Compute final WCS
    WCS = 0.0
    for j in range(k):
        cluster_points = X[cluster_assignments == j]
        if len(cluster_points) > 0:
            differences = np.abs(cluster_points - centroids[j])
            WCS += np.sum(np.sum(differences ** p, axis=1))

    ###########################################################################
    #                             END OF YOUR CODE                            #
    ###########################################################################
    return centroids, cluster_assignments, WCS
