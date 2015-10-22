The project contains two files,
1. MyApplication
2. PervasiveCourse_student

The first file is the android client which is an android project. It can be imported into android studio as a project and, installed on any android phone. We have also included the apk file which can be installed on any android phone.

The second file is the server that works on Raspberry pi. To compile the files open the console in the directory, 'ant build' will compile the files, while 'sudo ant JSONRPCServer' will bring up the server active.

The android application UI has one activity. The first part of the activity shows the current temperature and ambient of the room. The history of the room temperature is also stored at the bottom of the same page. 
The second page of this activity is meant to set and delete rules.  