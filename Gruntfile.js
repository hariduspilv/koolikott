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
                src: ['bower_components/jquery/dist/jquery.min.js', 'bower_components/angular/angular.min.js', 'bower_components/angular-route/angular-route.min.js', 'bower_components/bootstrap-sass/assets/javascripts/bootstrap.min.js'],
                dest: '<%= project.assets %>/js/dop.min.js',
            }
        },
        uglify: {
            options: {
            },
            my_target: {
                files: {
                    '<%= project.assets %>/js/dop.min.js': ['<%= project.assets %>/js/dop.min.js'],
                    '<%= project.assets %>/js/require.min.js': ['bower_components/requirejs/require.js'],
                    '<%= project.assets %>/js/modernizr.min.js': ['bower_components/modernizr/modernizr.js']
                }
            }
        },
        sass: {
            dev: {
                options: {
                    style: 'expanded',
                    compass: false,
                    sourcemap: 'none'
                    
                },
                files: {
                    '<%= project.assets %>/css/dop.min.css':'<%= project.css %>'
                }
            }
        },
        watch: {
            sass: {
                files: 'sass/{,*/}*.{scss,sass}',
                tasks: ['sass:dev']
            }
        },
        clean: ["assets", "dop.tar.gz"],
    	compress: {
    		main: {
    			options: {
    				archive: 'dop.tar.gz',
    				mode: 'tgz'
    			},
    			src: ['app/**/*', 'assets/**/*', 'index.html']
    		}
		}
    });

    grunt.loadNpmTasks('grunt-contrib-sass');
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-contrib-clean');
    grunt.loadNpmTasks('grunt-contrib-compress');

    grunt.registerTask('build', ['clean', 'concat', 'uglify', 'sass']);
    grunt.registerTask('package', ['build', 'compress']);

};