'use strict';

var spawn = require('win-spawn');
var chalk = require('chalk');

var app = 'gem';
var args = ['install', 'sass', 'dnode'];
var command = app + ' ' + args.join(' ');

console.log(chalk.cyan('\n\nInstalling required Ruby gems...'));
console.log(chalk.cyan('$ ') + command + '\n');

var gem = spawn(app, args, {
    stdio: 'inherit'
});

gem.on('error', function (err) {
    console.error(chalk.red('\nFailed to install sass and dnode gems.\n'));
    console.error(err.stack);
    console.log(
        chalk.cyan('\nPlease ensure you have Ruby installed, then run:\n\n') +
        chalk.cyan('    $ ') + command
    );
    process.exit(1);
});

gem.on('exit', function (code) {
    if (code === 0) {
        console.log(chalk.green('\nSuccess: sass and dnode gems are installed.\n'));
    }
    else {
        console.error(chalk.red('\nError: gem process exited with code ' + code + '\n'));
        process.exit(1);
    }
});
