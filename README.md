### App version to DB version
| App version code | App version name | DB version |
|:----------------:|:----------------:|:----------:|
| 1                | 1.0.0            | 1          |
| 2                | 1.1.0            | 2          |
| 3                | 2.0.0            | 3          |


### Get the DB from virtual device
- adb shell
- su
- chmod -R 777 /data
- ctrl+D (exit shell) twice
- adb root
- cd to_intended_folder
- adb pull /data/data/feelings.guide/databases/FeelingsGuide.db

### Release
git tag -a 2.0.0 -m "Release 2.0.0"
git tag
git push origin --tags

