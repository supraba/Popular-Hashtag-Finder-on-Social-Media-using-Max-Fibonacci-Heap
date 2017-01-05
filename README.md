# Popular-Hashtag-Finder-on-Social-Media-using-Max-Fibonacci-Heap
Implemented a system to find the n most popular hashtags on social media such as Facebook or Twitter. 

Operations supported: insert hashtags and their count, increase a hashtags's count, find the most popular hashtag, remove n popular hashtags, merge two Fibonacci heaps and recursively cut the marked parents of a node.  

Structures used for the implementation: 
1. Max Fibonacci heap: used to keep track of the frequencies of hashtags. 
2. Hash table: contains hashtag as the key and pointer to the corresponding node in the Fibonacci Heap as the value.
