require 'rubygems'
require 'sass'
require 'dnode'

$port = Integer(ARGV[0])

# NOTE - can't work out why this won't work when placed at the bottom, but
# seems to work here anyway for some reason...
STDOUT.write "ready\n"
STDOUT.flush


DNode.new({
  :f => proc { |options, cb|
    sass_file = options[:filename]

    real_options = {
      filename: sass_file
    }

    if options[:sourcemap]
      css_path = sass_file.sub(/[^.]+\z/, "css")
      sourcemap_path = "#{css_path}.map"

      real_options[:sourcemap] = true
      real_options[:css_path] = css_path
      real_options[:sourcemap_filename] = sourcemap_path
    end

    if options[:loadPaths]
      real_options[:load_paths] = options[:loadPaths]
    end

    engine = Sass::Engine.for_file sass_file, real_options

    begin
      if real_options[:sourcemap]
        css, sourcemap = engine.render_with_sourcemap(File.basename(sourcemap_path))

        cb.call({
          file: sass_file,
          css: css,
          sourcemap: sourcemap.to_json({
            css_path: css_path,
            sourcemap_path: sourcemap_path
          })
        })
      else
        css = engine.render

        cb.call({
          file: sass_file,
          css: css
        })
      end
    rescue Sass::SyntaxError => e

      cb.call({
        file: sass_file,
        error: e.message,
        sass_line: e.sass_line,
        sass_template: e.sass_template,
        sass_backtrace: e.sass_backtrace
      })

    end
  }
}).listen($port)
