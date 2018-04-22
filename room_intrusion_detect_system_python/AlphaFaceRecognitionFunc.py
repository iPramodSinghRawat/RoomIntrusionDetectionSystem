'''
AlphaFaceRecognitionFunc.py
By: Pramod Singh Rawat
'''

import cv2
import os

#Note: Fast and not able to detect some faces
#open_cv_xml_2_detect_face ='cascades_data/lbpcascades/lbpcascade_frontalface.xml'

#Note: litle bit slow # But Working Fine detecting Most face
open_cv_xml_2_detect_face ='cascades_data/haarcascades/haarcascade_frontalface_alt.xml'

#Note: in Multiple Detection Detecting Non Face
#open_cv_xml_2_detect_face ='cascades_data/haarcascades/haarcascade_frontalface_default.xml'

#Note: not able to detect some faces
#open_cv_xml_2_detect_face ='cascades_data/haarcascades/haarcascade_frontalface_alt2.xml'

face_cascade = cv2.CascadeClassifier(open_cv_xml_2_detect_face)
#to detect face
def detect_face(img):

    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    faces = face_cascade.detectMultiScale(gray, scaleFactor=1.2, minNeighbors=5)
    '''
    faces = face_cascade.detectMultiScale(
        gray,
        scaleFactor=1.1,
        minNeighbors=5,
        minSize=(30, 30),
        flags=cv2.COLOR_BGR2HSV
    )
    '''
    if (len(faces) == 0):
        return None, None

    (x, y, w, h) = faces[0]

    return gray[y:y+w, x:x+h], faces[0]

def detect_multiple_o_face(img):
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    faces = face_cascade.detectMultiScale(gray,scaleFactor=1.2, minNeighbors=5)
    return faces

def prepare_training_data_type1(data_folder_path, for_label, faces_main, labels_main):

    label = int(for_label)
    subject_images_names = os.listdir(data_folder_path)
    for image_name in subject_images_names:

        if image_name.startswith("."):
            continue;

        image_path = data_folder_path + "/" + image_name

        #image reading
        image = cv2.imread(image_path)

        cv2.imshow("Training on image...", cv2.resize(image, (400, 500)))
        cv2.waitKey(100)

        #detect face
        face, rect = detect_face(image)

        if face is not None:
            #add face to list of faces
            faces_main.append(face)
            #add label for this face
            labels_main.append(label)

    cv2.destroyAllWindows()
    cv2.waitKey(1)
    cv2.destroyAllWindows()

    return None

def prepare_training_data_type2(data_folder_path,label):
    faces = []
    labels = []

    subject_images = os.listdir(data_folder_path)

    for image_file in subject_images:

        if image_file.startswith("."):
            continue;

        image_path =  data_folder_path+"/"+image_file
        #print(image_path)

        image = cv2.imread(image_path)#image reding

        cv2.imshow("Training on image...", cv2.resize(image, (400, 500)))#show the image
        cv2.waitKey(100)

        #detect face
        face, rect = detect_face(image)

        if face is not None:
            faces.append(face)
            labels.append(label)

    cv2.destroyAllWindows()
    cv2.waitKey(1)
    cv2.destroyAllWindows()

    return faces, labels

#draw rectangle on Face
def draw_rectangle(img, rect):
    (x, y, w, h) = rect
    cv2.rectangle(img, (x, y), (x+w, y+h), (0, 255, 0), 2)

#draw text
def draw_text(img, text, x, y):
    cv2.putText(img, text, (x, y), cv2.FONT_HERSHEY_PLAIN, 1.5, (0, 255, 0), 2)

def predict(face_recognizer, subjects, test_img):

    img = test_img.copy()

    face, rect = detect_face(img)

    if face is None:
        return None
    else:
        #predicting the face using our face recognizer
        label, confidence = face_recognizer.predict(face)
        print(label)
        print(confidence)
        match_percentage=round(100-float(confidence),2)

        try:
            if match_percentage < 0:
                label_text = "Detect"
            else:
                label_text = subjects[label]+", Match: "+str(match_percentage)+" %"
                #label_text = str(label)
        except IndexError:
            label_text = 'UnKnown'

        draw_rectangle(img, rect)
        draw_text(img, label_text, rect[0], rect[1]-5)

        return img

#function to recognise A Face from Multiple Faces Image File
def predict_from_multiple(face_recognizer,subjects,test_img):

    #copy of the image
    img = test_img.copy()
    faces = detect_multiple_o_face(img)

    i=0
    for (x, y, w, h) in faces:
        rect = (x, y, w, h)

        detc_face = img[y:y+h, x:x+w]
        FaceFileName = "detected_faces/face_" + str(i) + ".jpg"# savinf faces in separete directory
        cv2.imwrite(FaceFileName, detc_face)

        #predicting the face using our face recognizer
        label, confidence = face_recognizer.predict(cv2.cvtColor(detc_face, cv2.COLOR_BGR2GRAY))
        match_percentage=round(100-float(confidence),2)

        print("label: "+str(label)+" match_percentage: "+str(match_percentage))

        #Getting Data of Face Detected using Label
        try:
            if match_percentage < 30:
                label_text = "Detect"
            else:
                label_text = subjects[label]+", Match: "+str(match_percentage)+" %"
        except IndexError:
            label_text = 'UnKnown'

        #draw a rectangle around face detected
        draw_rectangle(img, rect)
        #draw name of predicted person
        draw_text(img, label_text, rect[0], rect[1]-5)
        i += 1

    return img
