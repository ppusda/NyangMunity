rm ../src/main/resources/static/index.html
rm -rf ../src/main/resources/static/assets/
npm run build
mv -f dist/assets/ ../src/main/resources/static/
mv dist/index.html ../src/main/resources/static/