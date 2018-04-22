#RIDSFirebaseFuncs.py

import time
import pyrebase
import asyncio

from pyfcm import FCMNotification

config = {
"apiKey": "AIzaSyDJ79jU74MGbZ-563Px1po0GXUXr6Qqr6Q",
"authDomain": "aurids-alpha1.firebaseapp.com",
"databaseURL": "https://aurids-alpha1.firebaseio.com",
"storageBucket": "aurids-alpha1.appspot.com",
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
    push_service = FCMNotification(api_key="AAAAoHcx9cg:APA91bFN1Rygh0nAKJ5irk5DgGr-lN_dTtn-SjJrdmLRuZSJg6WvI5rzt892kWM9LDWUOGJBO08OFMtMN2AjBz9b_GqLE0ogOjlQCIfsDIViLzM7GSliru2pX2vmO1avupSmASIvFb7K")

    #id of device to send push notifications
    registration_id = "eQlSS4m7ys0:APA91bHmxAqtt_cV4uO0XA2u3EOzKvABp66OKKl8-3bR-fPHsBcJLBWTZU8Ww1A0si3bb0ySalvvsnIWjCgHfpFhwoQgt5v711uGwzn5YNivRUnFN_Vhau6LTgzuXd8EoXQVpzI6qyV8"

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
