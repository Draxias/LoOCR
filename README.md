LoOCR
=====
Proof-of-concept Android app that applies Google Places information to refine OCR results from teseeract-OCR

Required API Keys:
  Google Places
  Google Maps
  
Tesseract Installation:
Copy the "tessdata" folder into your root folder on your Android device. 
  These are the language files required to run the Tesseract API. You may,
  if you wish, place the folder elsewhere on your sd card, but you will need
  to change the file path in the app's WebloaderActivity.java source code from:
      String DATA_PATH = myDir.toString() + "/";
  to your new filepath.
  
  
Remember to have your device's wireless turned on, as well as GPS capabilities.
