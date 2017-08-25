# 1. What this Program does?
This project was to implement a content-based image retrieval system based on 3 different color histogram comparison methods (Intensity, color code and hybird of both) with a relevance feedback option. I also developed a graphic user interface that allows users to browse the image dataset, select the query image, and view the retrieved images. 

# 2. Demo
Download jar file [CBIR.jar](https://github.com/emily0707/Graphic-user-Interface-for-Cancer-Research/blob/master/Content-Based-Image-Retrieval-System-/CBIR.jar ).
   ![alt text](https://github.com/emily0707/Graphic-user-Interface-for-Cancer-Research/blob/master/images/CBIR.png "Demo ScreenShot")
   
# 3. Challege encountered
### 1. How to improve precision?    
The challege part of this project is how to get a higher precision since the precsion was only 60% using color histogram comparision methods.     
Solution : Implement a feature called user feedback, and improved prcision from 60% to 70%. 
// to be contiune, how to calcalute user feedback........
### 2. How to improve performance?   
Becasue color histograms comparation methods invovles a lot of calculation. Becuase before providing users the query results, first the program has to generate intensity, color-code, hybird data and serach result indexes for each picture. It takes 5-10 minutes to run the calculation. Which is not user-friendly.      
Solution: add a preprocess feature enables users get query results immediately.      
I preprocessed impages in the database. So for existing pictures in the database, after choosing a query image, users can get results immediatly.      
If a user udpates images in the database, then she can click "preprocess" button to recalculation before start a query. 


# How to run your program:Option   
1: Download and click the executable “CBIR.jar”Option     
2: follow the steps as below:    
- 1. Open the command prompt    
- 2. Set the path to where java jar file is located on your system.     
- 3. Type command “java –jar ./CBIR.jar” and press enter.    

# How to use your system with step-by-step instructions:    
1. After open the system, you can use “previous page” and “next page” to select the target picture for a query.     
2. Click a thumbnail image and then the big images will appear. And the system set the image as a query image.     
3. All the 100 images are preprocessed and results are stored in text file. After you click the “Intensity”, “Color Code” or “Color Code & Intensity” button, you will to get the query result immediately without waiting.      
4. Click the “Relevant” check box. Then checking boxes for each image will appears below.       
5. The query image is by default is relevant. You can check the checking boxes based on your understanding of relevant. Then you click the “Color Code and Intensity” button. The query result will be refreshed base on the new feedback inputs.     
6. You can do relevant iterations as many times as you want. I implemented relevant checking boxes for 100 images.      
7. After you are done, you can either click “Reset” button to reset the system to theoriginal state, or you can click close icon on the right corner to close the system.     
8. “Preprocess” button: you don’t need to worry about it. However, if one user updates some images in database, then he/she needs to click “preprocess” button to update data before start a query. It will take about 5-10 minutes to process. Because we need to generate intensity, color-code data, Color Code& Intensity and search result indexes for each picture.  It involves a lot of calculation. 

