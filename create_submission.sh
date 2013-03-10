
cd ..

mkdir -p submission
rm -r submission
mkdir submission
mkdir submission/securedit

cp -r cs5430_project/
cp -r cs5430_project/*.sh submission/
cp -r cs5430_project/securedit/src submission/securedit
cp -r cs5430_project/securedit/test submission/securedit
rm -rf submission/securedit/src/_reference_classes

cp cs5430_project/securedit/dist/securedit.jar submission/securedit.jar
cp cs5430_project/keygen.jar submission/keygen.jar

echo "I'm a really big secret" > submission/test_file_for_encryption.txt

tar -cvzf submission.tar.gz submission
