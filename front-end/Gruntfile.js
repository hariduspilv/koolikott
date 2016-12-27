'use strict';

module.exports = function (grunt) {

    // Load grunt tasks automatically
    require('load-grunt-tasks')(grunt);

    // Time how long tasks take. Can help when optimizing build times
    require('time-grunt')(grunt);

    grunt.loadNpmTasks('grunt-postcss');
    grunt.loadNpmTasks('grunt-webdriver');
    grunt.loadNpmTasks('grunt-selenium-standalone');
    grunt.loadNpmTasks('grunt-shell-spawn');
    grunt.loadNpmTasks('grunt-env');

    // Configurable paths for the application
    var appConfig = {
        app: require('./bower.json').appPath || 'app',
        dist: {
            app: 'dist/dop',
            folder: 'dist'
        }
    };

    // Define the configuration for all the tasks
    grunt.initConfig({

        // Project settings
        yeoman: appConfig,

        // Watches files for changes and runs tasks based on the changed files
        watch: {
            js: {
                files: [
                    '<%= yeoman.app %>/directives/**/**/*.js',
                    '<%= yeoman.app %>/services/**/**/*.js',
                    '<%= yeoman.app %>/views/**/**/*.js',
                    '<%= yeoman.app %>/utils/**/**/*.js'
                ],
                tasks: ['newer:jshint:all'],
                options: {
                    livereload: '<%= connect.options.livereload %>'
                }
            },
            sass: {
                files: ['<%= yeoman.app %>/styles/{,*/}*.{scss,sass}'],
                tasks: ['sass:server', 'postcss:dist']
            },
            gruntfile: {
                files: ['Gruntfile.js']
            },
            livereload: {
                options: {
                    livereload: '<%= connect.options.livereload %>'
                },
                files: [
                    '<%= yeoman.app %>/views/**/**/*.html',
                    '<%= yeoman.app %>/directives/**/**/*.html',
                    '<%= yeoman.app %>/utils/**/**/*.html',
                    '.tmp/styles/{,*/}*.css',
                    '<%= yeoman.app %>/images/{,*/}*.{png,jpg,jpeg,gif,webp,svg}'
                ]
            }
        },

        // The actual grunt server settings
        connect: {
            options: {
                port: 3001,
                // Change this to '0.0.0.0' to access the server from outside.
                hostname: '0.0.0.0',
                livereload: 3729
            },
            proxies: [{
                context: '/rest',
                host: 'oxygen.netgroupdigital.com',
                port: 8080
            }],
            livereload: {
                options: {
                    open: true,
                    middleware: function (connect) {
                        // Setup the proxy
                        var middlewares = [require('grunt-connect-proxy/lib/utils').proxyRequest];

                        middlewares.push(require('connect-modrewrite')(['!(\\..+)$ / [L]']));

                        middlewares.push(connect.static('.tmp'));

                        middlewares.push(connect().use(
                            '/app/styles',
                            connect.static('./app/styles')
                        ));

                        // Make directory browse-able.
                        middlewares.push(connect.static(appConfig.app));

                        return middlewares;
                    }
                }
            },
            dist: {
                options: {
                    open: true,
                    middleware: function (connect, options, middlewares) {
                        // Setup the proxy
                        var proxy = require('grunt-connect-proxy/lib/utils').proxyRequest;
                        middlewares.push(proxy);

                        // gzip
                        var compression = require('compression');
                        middlewares.unshift(compression({level: 9}));

                        // Make directory browse-able.
                        middlewares.unshift(connect.static(appConfig.dist.app));

                        return middlewares;
                    }
                }
            }
        },

        // Make sure code styles are up to par and there are no obvious mistakes
        jshint: {
            options: {
                jshintrc: '.jshintrc',
                reporter: require('jshint-stylish')
            },
            all: {
                src: [
                    //'Gruntfile.js',
                    // Commented out because .jshintrc is not correctly configured to follow DÖP developers style
                    //'<%= yeoman.app %>/**/**/*.js'
                ]
            }
        },

        // Empties folders to start fresh
        clean: {
            dist: {
                files: [{
                    dot: true,
                    src: [
                        '.tmp',
                        '<%= yeoman.dist.folder %>/{,*/}*',
                        '!<%= yeoman.dist.folder %>/.git{,*/}*'
                    ]
                }]
            },
            server: '.tmp'
        },

        // Add vendor prefixed styles
        postcss: {
            options: {
                map: true,
                processors: [
                    require('autoprefixer')({
                        browsers: ['last 2 versions', 'last 3 iOS versions']
                    })
                ]
            },
            dist: {
                src: '.tmp/styles/{,*/}*.css'
            }
        },

        sass: {
            options: {
                includePaths: [
                    '<%= yeoman.app %>/libs'
                ]
            },
            dist: {
                files: [{
                    expand: true,
                    cwd: '<%= yeoman.app %>/styles',
                    src: ['*.scss'],
                    dest: '.tmp/styles',
                    ext: '.css'
                }]
            },
            server: {
                files: [{
                    expand: true,
                    cwd: '<%= yeoman.app %>/styles',
                    src: ['*.scss'],
                    dest: '.tmp/styles',
                    ext: '.css'
                }]
            }
        },

        // Renames files for browser caching purposes
        filerev: {
            dist: {
                src: [
                    '<%= yeoman.dist.app %>/styles/{,*/}*.css',
                    '<%= yeoman.dist.app %>/images/{,*/}*.{png,jpg,jpeg,gif,webp,svg}',
                    '<%= yeoman.dist.app %>/fonts/*',
                    '<%= yeoman.dist.app %>/js/*'
                ]
            }
        },

        // Reads HTML for usemin blocks to enable smart builds that automatically
        // concat, minify and revision files. Creates configurations in memory so
        // additional tasks can operate on them
        useminPrepare: {
            html: '<%= yeoman.app %>/index.html',
            options: {
                dest: '<%= yeoman.dist.app %>'
            }
        },

        // Performs rewrites based on filerev and the useminPrepare configuration
        usemin: {
            html: ['<%= yeoman.dist.app %>/{,*/}*.html'],
            css: ['<%= yeoman.dist.app %>/styles/{,*/}*.css'],
            options: {
                assetsDirs: [
                    '<%= yeoman.dist.app %>',
                    '<%= yeoman.dist.app %>/images',
                    '<%= yeoman.dist.app %>/styles'
                ]
            }
        },

        imagemin: {
            dist: {
                files: [{
                    expand: true,
                    cwd: '<%= yeoman.app %>/images',
                    src: '{,*/}*.{png,jpg,jpeg,gif}',
                    dest: '<%= yeoman.dist.app %>/images'
                }]
            }
        },

        svgmin: {
            dist: {
                files: [{
                    expand: true,
                    cwd: '<%= yeoman.app %>/images',
                    src: '{,*/}*.svg',
                    dest: '<%= yeoman.dist.app %>/images'
                }]
            }
        },

        htmlmin: {
            dist: {
                options: {
                    removeComments: true,
                    collapseWhitespace: true,
                    conservativeCollapse: true,
                    collapseBooleanAttributes: true,
                    collapseInlineTagWhitespace: true,
                    removeCommentsFromCDATA: true,
                    removeEmptyAttributes: true,
                    removeOptionalTags: true
                },
                files: [{
                    expand: true,
                    cwd: '<%= yeoman.dist.app %>',
                    src: ['*.html', 'views/{,*/}*.html'],
                    dest: '<%= yeoman.dist.app %>'
                }]
            }
        },

        // ng-annotate tries to make the code safe for minification automatically
        // by using the Angular long form for dependency injection.
        ngAnnotate: {
            dist: {
                files: [{
                    expand: true,
                    src: [
                        '<%= yeoman.app %>/*.js',
                        '<%= yeoman.app %>/directives/**/**/*.js',
                        '<%= yeoman.app %>/services/**/**/*.js',
                        '<%= yeoman.app %>/views/**/**/*.js',
                        '<%= yeoman.app %>/utils/**/**/*.js'
                    ],
                    dest: '.tmp'
                }]
            }
        },

        // Replace Google CDN references
        cdnify: {
            dist: {
                html: ['<%= yeoman.dist.app %>/*.html']
            }
        },

        // Copies remaining files to places other tasks can use
        copy: {
            dist: {
                files: [{
                    expand: true,
                    dot: true,
                    cwd: '<%= yeoman.app %>',
                    dest: '<%= yeoman.dist.app %>',
                    src: [
                        '*.{ico,png,txt}',
                        '.htaccess',
                        '*.html',
                        '*.js',
                        'views/**/**/*.html',
                        'images/{,*/}*.{webp}',
                        'fonts/{,*/}*.*',
                        'directives/**/**/*.html',
                        'utils/**/**/*.{html,ttf,png,css}',
                        'utils/preloader/*.*'
                    ]
                }, {
                    expand: true,
                    cwd: '<%= yeoman.app %>/libs',
                    dest: '.tmp/<%= yeoman.app %>/libs',
                    src: [
                        '**/*.js'
                    ]
                }, {
                    expand: true,
                    cwd: '.tmp/images',
                    dest: '<%= yeoman.dist.app %>/images',
                    src: ['generated/*']
                }]
            },
            styles: {
                expand: true,
                cwd: '<%= yeoman.app %>/styles',
                dest: '.tmp/styles/',
                src: '{,*/}*.css'
            }
        },

        // Run some tasks in parallel to speed up the build process
        concurrent: {
            server: [
                'sass:server'
            ],
            test: [
                'sass'
            ],
            dist: [
                'sass',
                'imagemin',
                'svgmin'
            ]
        },

        // Create compressed archive for deployment
        compress: {
            dev: {
                options: {
                    archive: '<%= yeoman.dist.folder %>/kott.tar.gz',
                    mode: 'tgz'
                },
                expand: true,
                cwd: '<%= yeoman.dist.folder %>/',
                src: ['dop/**/*']
            },
            live: {
                options: {
                    archive: '<%= yeoman.dist.folder %>/kott.tar.gz',
                    mode: 'tgz'
                },
                expand: true,
                cwd: '<%= yeoman.dist.folder %>/',
                src: [
                    'dop/**/*',
                    '!dop/views/dev/**'
                ]
            },
        },

        webdriver: {
            test1: {
                configFile: './wdio.conf.js'
            },
            test2: {
                configFile: './wdio.conf2.js'
            }
        },
        'selenium_standalone': {
            serverConfig: {
                seleniumVersion: '2.50.1',
                seleniumDownloadURL: 'http://selenium-release.storage.googleapis.com',
                drivers: {
                    chrome: {
                        version: '2.20',
                        arch: process.arch,
                        baseURL: 'http://chromedriver.storage.googleapis.com'
                    }
                }
            }
        },
        shell: {
            xvfb: {
                command: 'Xvfb :99 -ac -screen 0 1920x1080x24',
                options: {
                    async: true
                }
            }
        },
        env: {
            xvfb: {
                DISPLAY: ':99'
            }
        },

        ngconstant: {
            dist: {
                options: {
                    dest: 'dist/dop/constants.js',
                    name: 'DOPconstants'
                },
                constants: {
                    APP_VERSION: grunt.file.readJSON('package.json').version,
                    FB_APP_ID: '225966171178748'
                }
            }
        },

        // ES6 syntax
        babel: {
            options: {
                presets: ['es2015']
            },
            dist: {
                files: {
                    '.tmp/concat/js/config.js': '.tmp/concat/js/config.js',
                    '.tmp/concat/js/controllers.js': '.tmp/concat/js/controllers.js',
                    '.tmp/concat/js/directives.js': '.tmp/concat/js/directives.js',
                    '.tmp/concat/js/services.js': '.tmp/concat/js/services.js'
                }
            }
        }
    });

    grunt.registerTask('serve', 'Compile then start a connect web server', function (target) {
        if (target === 'dist') {
            return grunt.task.run(['configureProxies:server', 'connect:dist:keepalive']);
        }

        grunt.task.run([
            'clean:server',
            'concurrent:server',
            'postcss:dist',
            'configureProxies:server',
            'connect:livereload',
            'watch'
        ]);
    });

    grunt.registerTask('build', [
        'clean:dist',
        'useminPrepare',
        'concurrent:dist',
        'postcss:dist',
        'concat',
        'babel',
        'ngAnnotate',
        'copy:dist',
        'ngconstant:dist',
        'cdnify',
        'cssmin',
        'uglify',
        'filerev',
        'usemin',
        'htmlmin'
    ]);

    grunt.registerTask('default', [
        'newer:jshint',
        'build'
    ]);

    grunt.registerTask('package', [
        'build',
        'compress:dev'
    ]);

    grunt.registerTask('package-live', [
        'build',
        'compress:live'
    ]);

    grunt.registerTask('regression-test', 'Run visual regression tests.', [
        'shell:xvfb',
        'env:xvfb',
        'selenium_standalone:serverConfig:install',
        'selenium_standalone:serverConfig:start',
        'webdriver:test1',
        'webdriver:test2',
        'selenium_standalone:serverConfig:stop'
    ]);
};
