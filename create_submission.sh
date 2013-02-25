
cd ..

mkdir -p submission
rm -r submission
mkdir submission
mkdir submission/securedit

cp -r cs5430_project/
cp -r cs5430_project/securedit/src submission/securedit
cp -r cs5430_project/securedit/test submission/securedit
rm -rf submission/securedit/src/_reference_classes

cp cs5430_project/securedit/dist/securedit.jar submission/securedit.jar
cp cs5430_project/keygen.jar submission/keygen.jar

echo "I'm a really big secret" > submission/test_file_for_encryption.txt
echo "java -jar keygen.jar" > submission/keygen.sh
echo "java -jar securedit.jar 0 localhost 4001" > submission/demo1.sh
echo "java -jar securedit.jar 1 localhost 4002" > submission/demo2.sh
chmod +x submission/keygen.sh
chmod +x submission/demo1.sh
chmod +x submission/demo2.sh

tar -cvzf submission.tar.gz submission
