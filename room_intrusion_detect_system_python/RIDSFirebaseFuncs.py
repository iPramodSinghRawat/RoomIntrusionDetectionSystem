#RIDSFirebaseFuncs.py

import time
import pyrebase
import asyncio

from pyfcm import FCMNotification

config = {
"apiKey": "project-apikey",
"authDomain": "project.firebaseapp.com",
"databaseURL": "https://project.firebaseio.com",
"storageBucket": "project.appspot.com",
"serviceAccount": "firebase-adminsdk-serviceaccounts.json"
}

firebase = pyrebase.initialize_app(config)
#file_to_upload = "image_rec/test_image1.jpg"
#file_to_upload_as = "image_rec/test_image1.jpg"

#@asyncio.coroutine
#async
def upload_file_2_firebase(file_to_upload_as,file_to_upload):
    #storage
    storage = firebase.storage()
    #first path in firebase storage another path in local storage
    #upload
    #await asyncio.sleep(1)
    storage.child(file_to_upload_as).put(file_to_upload) #working
    #download
    #storage.child("image_rec/unknown_image.jpg").download("downloaded.jpg") #working

def upload_notification_2_firebase(face_file,face_frame_file,notification,notification_dtls):
    #main_table
    notifications_table = "notifications"
    #database
    db = firebase.database()
    data = {
    "notification": notification,
    "details":notification_dtls,
    "face_file":face_file,
    "face_frame_file":face_frame_file,
    "timestamp":time.time()
    }
    rec = db.child(notifications_table).push(data)
    #print(rec)
    last_push_key = rec['name']
    print(last_push_key)
    return last_push_key

def upload_notification_2_firebase_v2(face_file,face_frame_file,notification,notification_dtls):
    #main_table
    notifications_table = "notifications"
    #database
    db = firebase.database()
    data = {
    "notification": notification,
    "details":notification_dtls,
    "face_file":face_file,
    "face_frame_file":face_frame_file,
    "timestamp":time.time()
    }
    rec = db.child(notifications_table).push(data)
    print(rec)
    last_push_key = rec['name']
    print(last_push_key)

def push_notification_via_firebase(last_push_key,message_title,message_body):

    #api_key=server_key
    push_service = FCMNotification(api_key="from-firebase-acount")

    #id of device to send push notifications
    registration_id = "put-your-android-device-id"

    # Sending a notification with data message payload
    data_message = {
        "Title" : message_title,
        "body" : message_body,
        "last_push_key" : last_push_key
    }
    # To multiple devices
    #result = push_service.notify_multiple_devices(registration_ids=registration_ids, message_body=message_body,sound='default', data_message=data_message)
    #to single with message body
    result = push_service.notify_single_device(registration_id=registration_id, message_body=message_body,sound='default', data_message=data_message)
