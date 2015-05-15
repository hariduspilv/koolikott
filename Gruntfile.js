'use strict';

module.exports = function(grunt) {

    grunt.initConfig({

        pkg: grunt.file.readJSON('package.json'),

        project: {
            app: ['app'],
            assets: ['assets'],
            css: ['sass/style.scss']
        },
        concat: {
            options: {
                separator: ';\n'
            },
            dist: {
                src: ['bower_components/requirejs/require.js', 'bower_components/jquery/dist/jquery.min.js', 'bower_components/angular/angular.min.js', 'bower_components/angular-route/angular-route.min.js', 'bower_components/bootstrap-sass/assets/javascripts/bootstrap.min.js'],
                dest: '<%= project.assets %>/js/dop.js',
            }
        },
        uglify: {
            options: {
            },
            my_target: {
                files: {
                    '<%= project.assets %>/js/dop.min.js': ['<%= project.assets %>/js/dop.js']
                }
            }
        },
        sass: {
            dev: {
                options: {
                    style: 'expanded',
                    compass: false
                },
                files: {
                    '<%= project.assets %>/css/style.css':'<%= project.css %>'
                }
            }
        },
        watch: {
            sass: {
                files: 'sass/{,*/}*.{scss,sass}',
                tasks: ['sass:dev']
            }
        }
    });

    grunt.loadNpmTasks('grunt-contrib-sass');
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-uglify');

    grunt.registerTask('default', ['watch', 'concat', 'uglify']);

};