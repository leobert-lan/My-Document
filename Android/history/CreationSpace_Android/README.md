# VsoCyy

This is a project that contains all server-channels of landhightech Co.,Ltd.

Here are all folders in the VCS:

- apk_release_backup:all release install-packages are sorted by version;
- buildTemp:we ignore the version controlling for the *.apk files in this folder,set the output destination of debug.apk to this folder;
- code:for code;
- design:storage for the UI files,serverd as a backup and collection;
- doc:not the doc for code,storage some important information such as:3rd platform account and app info, core design, and so on;
- key:the Signature-Key and the password information
- prototype: The PM is our favorite! -- this is a truth.

Branch Rules:

- main works in 'develop'
- control by 'master'
- some complicated and indipendant should in 'feature'
- bug fix in QA(SIT & UAT),publish should in 'release'
- uh~~uuh 'hotfix', headache!Your are in big trouble! Try to raise product quality to avoid use it! 

by leobert.lan 
2017-1-19 10:37:10
