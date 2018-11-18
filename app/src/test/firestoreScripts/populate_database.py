import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore
from faker import Faker

cred = credentials.Certificate('./agora-949cf-firebase-adminsdk-rxhft-ce9b6aba95.json')
firebase_admin.initialize_app(cred)

db = firestore.client()
users_ref = db.collection(u'users')

fake = Faker()

for i in range(100):
  fake_name = fake.name()
  users_ref.document(str(i).decode(encoding='utf-8')).set({
    u'display_name' : fake_name
  })
  # print(fake_name.decode(encoding='utf-8'))

docs = users_ref.get()

for doc in docs:
  print(u'{} => {}'.format(doc.id, doc.to_dict()))
