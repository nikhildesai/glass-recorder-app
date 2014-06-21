glass-recorder-app
==================

Recorder app for Google glass

1. To build the app, you need to clone the repository

git clone https://github.com/nikhildesai/glass-recorder-app.git

2. Import the project into Eclipse (.classpath and .project files are included)

3. Right-click on project and select 'Run As' ->  'Android Application'


___________________

The app was built during a hackathon and works with a server that accepts the following requests:

1. POST /lecture/create Multipart form data, upload file with name 'audio' and of type 3GP This will return a lecture resource that contains an ID, you must use this ID to upload notes

2. POST /lecture/add_photo/{id} Multipart form data, upload file with name 'photo' and type JPEG Another key with name timeStamp and value of milliseconds since audio start
