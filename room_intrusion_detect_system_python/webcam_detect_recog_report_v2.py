'''
webcam_detect_recog_report_v2.py
By: Pramod Singh Rawat
Author(Git): iPramodSinghRawat
Thanks To: gilbertfrancois
'''

import cv2
import sys
import time
#import numpy as np
import threading

from videocaptureasync import VideoCaptureAsync
from AlphaFaceRecognitionFunc import *
from FaceRecDataSet import *
from RIDSFirebaseFuncs import *

#wheel = ('-', '/', '|', '\\')
#wait = animation.Wait(wheel)

'''
Notes: Need Proper lighting on face for better detects
'''
def writeImageFiles2Drive(fileDtl, image):
    cv2.imwrite(fileDtl, image)

def performFirebaseAction(face_file,face_img,face_frame_file,face_frame_img):
    print("FirebaseAction Start")
    writeImageFiles2Drive(face_file, face_img)
    writeImageFiles2Drive(face_frame_file, face_frame_img)
    upload_file_2_firebase(face_file,face_file)
    upload_file_2_firebase(face_frame_file,face_frame_file)

    last_push_key = upload_notification_2_firebase(face_file,face_frame_file,"Intrusion Alert","Room Intrusion Detect")
    push_notification_via_firebase(last_push_key,"Intrusion Alert","Intruder Alert")
    print("FirebaseAction Done")

#face_rec_training_data_file = 'training_data_yml/face_rec_training_data_vid1.yml'
face_rec_training_data_file = 'training_data_yml/face_rec_training_data1.yml'

face_recognizer = cv2.face.LBPHFaceRecognizer_create()#LBPH face recognizer
face_recognizer.read(face_rec_training_data_file)

def start_recogniz(n_frames=500, width=1280, height=720, async=False):
    if async:
        cap = VideoCaptureAsync(0)
    else:
        cap = cv2.VideoCapture(0)
    cap.set(cv2.CAP_PROP_FRAME_WIDTH, width)
    cap.set(cv2.CAP_PROP_FRAME_HEIGHT, height)
    if async:
        cap.start()
    t0 = time.time()
    i = 0

    while i < n_frames:
        ret, frame = cap.read()

        faces = detect_multiple_o_face(frame)
        # Draw a rectangle around the faces
        for (x, y, w, h) in faces:
            detect_flg = 1
            ts = time.gmtime()
            ptimestamp=time.strftime("%s", ts)

            i += 1

            rect = (x, y, w, h)

            detc_face = frame[y:y+h, x:x+w]

            detectedFaceFileName = "image_detected_face/face_"+ptimestamp+"_" + str(i) + ".jpg"# saving faces in separete directory
            detectedFaceFrameFileName = "image_detect_face_frame/face_frame"+ptimestamp+"_" + str(i) + ".jpg"# saving frame in separete directory

            label, confidence = face_recognizer.predict(cv2.cvtColor(detc_face, cv2.COLOR_BGR2GRAY))
            print(label)
            match_percentage=round(100-float(confidence),2)

            try:
                if match_percentage < 0:
                    label_text = "Detect"
                    thread = threading.Thread(target=performFirebaseAction, args=(detectedFaceFileName,detc_face,detectedFaceFrameFileName,frame))
                    thread.start()
                elif match_percentage < 50:
                    thread = threading.Thread(target=performFirebaseAction, args=(detectedFaceFileName,detc_face,detectedFaceFrameFileName,frame))
                    thread.start()
                else:
                    label_text = subjects[label]+", Match: "+str(match_percentage)+" %"

            except IndexError:
                label_text = 'UnKnown'

            print(label_text)
            #draw a rectangle around face detected
            draw_rectangle(frame, rect)
            #draw name of predicted person
            draw_text(frame, label_text, rect[0], rect[1]-5)

        cv2.imshow('Frame', frame)

        if cv2.waitKey(1) & 0xFF == ord('q'):
            break
        #cv2.waitKey(1) & 0xFF

        i += 1
    print('[i] Frames per second: {:.2f}, async={}'.format(n_frames / (time.time() - t0), async))
    if async:
        cap.stop()
    cv2.destroyAllWindows()

if __name__ == '__main__':
    #start_recogniz(n_frames=500, width=1280, height=720, async=False)
    start_recogniz(n_frames=500, width=1280, height=720, async=True)
