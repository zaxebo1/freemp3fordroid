An open source, free: meaning Gratis, and Libre. Meaning free as in beer, and free as in speech. MP3 conversion library for developers to use in their Android apps.

A free app is distributed in the Android marketplace.
Instructions: Instruct your users to download the pname:org.freemp3droid . Then, use the following code:

```
Intent intent = new Intent("org.freemp3droid.CONVERT");
intent.putExtra("convertFile", "rawFile.pcm");
intent.putExtra("sampleRate",44100);
intent.putExtra("bitRate",192);

startActivity(intent);
```

convertFile: the raw pcm or wav data to convert to MP3. 16 Bit.

If you want to get a handle to the mp3 file, the convention is:
supply a convertFile of "hello.pcm" then, /sdcard/FreeMP3Droid/hello.mp3 will be there.

Enjoy !