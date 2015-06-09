echo 'Deleting existing .css and .map files in fixtures dir...'
rm test/fixtures/*.css
rm test/fixtures/*.map
echo 'Done.'
echo 'Rendering Sass fixtures...'
sass test/fixtures/simple.scss test/fixtures/simple.css
sass --sourcemap test/fixtures/simple-with-map.scss test/fixtures/simple-with-map.css
sass test/fixtures/complex.scss test/fixtures/complex.css
sass --sourcemap test/fixtures/complex-with-map.scss test/fixtures/complex-with-map.css
echo 'Done.'
