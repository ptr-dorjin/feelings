### App version to DB version
| App version code | App version name | DB version |
|:----------------:|:----------------:|:----------:|
| 1                | 1.0.0            | 1          |
| 2                | 1.1.0            | 2          |


### Get the DB from virtual device
1. adb shell
2. su
3. chmod -R 777 /data
4. adb root
5. ctrl+D (exit shell)
6. cd to_intended_folder
7. adb pull /data/data/feelings.guide/databases/FeelingsGuide.db

