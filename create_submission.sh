
cd ..

mkdir -p submission
rm -rf submission
mkdir submission

cp -r cs5430_project/securedit/src submission/
cp -r cs5430_project/securedit/test submission/test

cp -r cs5430_project/phase_2_documents submission/documents
cp -r cs5430_project/*.sh submission/

rm -rf submission/src/_old_stuff

cp cs5430_project/securedit/dist/securedit.jar submission/
cp -r cs5430_project/securedit/dist/lib submission/

#tar -cvzf submission.tar.gz submission
