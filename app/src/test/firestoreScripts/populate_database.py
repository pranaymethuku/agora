import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore

cred = credentials.Certificate('./agora-949cf-firebase-adminsdk-rxhft-ce9b6aba95.json')
firebase_admin.initialize_app(cred)

db = firestore.client()
users_ref = db.collection(u'users')
docs = users_ref.get()

for doc in docs:
    print(u'{} => {}'.format(doc.id, doc.to_dict()))
