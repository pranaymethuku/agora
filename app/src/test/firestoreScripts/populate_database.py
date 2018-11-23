import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore
from faker import Faker
import random

cred = credentials.Certificate('./agora-949cf-firebase-adminsdk-rxhft-ce9b6aba95.json')
firebase_admin.initialize_app(cred)

db = firestore.client()
users_ref = db.collection(u'users')
chats_ref = db.collection(u'chats')
num_users = 100
fake = Faker()

users = []
for i in range(num_users):
  fake_name = fake.name()
  users_ref.document(str(i).decode(encoding='utf-8')).set({
    u'display_name' : fake_name
  })
  users.append(fake_name)

# docs = users_ref.get()

# for doc in docs:
#   print(u'{} => {}'.format(doc.id, doc.to_dict()))
for i in range(num_users):
  recents = random.sample(range(num_users), random.randint(1, 12))
  chats_ref.document(str(i).decode(encoding='utf-8')).set({})
  for idx in recents:
    last_message = fake.sentence(nb_words=6, variable_nb_words=True)
    chats_ref.document(str(i).decode(encoding='utf-8')) \
      .collection(u'recent') \
      .document(str(idx).decode(encoding='utf-8')).set({
      u'display_name' : users[idx],
      u'last_message' : last_message
    })

recents = random.sample(range(num_users), random.randint(10, 20))
for idx in recents:
  last_message = fake.sentence(nb_words=6, variable_nb_words=True)
  chats_ref.document(u'xDlpksXYnmV1JixNEwzFhw0RPYn1').set({})
  chats_ref.document(u'xDlpksXYnmV1JixNEwzFhw0RPYn1') \
    .collection(u'recent') \
    .document(str(idx).decode(encoding='utf-8')).set({
    u'display_name' : fake.name(),
    u'last_message' : last_message
    })
