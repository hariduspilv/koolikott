const path = require('path');
const webpack = require('webpack');
const VueLoaderPlugin = require('vue-loader/lib/plugin')


module.exports = {
    mode: 'development',
    entry: './app/app.js',
    output: {
        path: __dirname + '/app',
        filename: 'bundle.js'
    },
    module: {
        rules: [

            {
                test: /\.css$/i,
                use: ['style-loader', 'css-loader'],
            },
            {
                test: /\.vue$/,
                loader: 'vue-loader'
            }
        ]
    },
    resolve: {
        extensions: ['.js', '.vue']
    },
    plugins: [
        new VueLoaderPlugin()
    ]
}
