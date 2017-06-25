require 'json'

start = {:str => ARGV.join(' ')}
puts JSON.generate(start)
