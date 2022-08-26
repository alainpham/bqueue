from http.server import BaseHTTPRequestHandler, HTTPServer
import bpy
import json
import sys
import logging
import os
import threading

# argv = sys.argv
# argv = argv[argv.index("--") + 1:]

# outputFolder=argv[0]
# blendFolder=argv[1]

outputFolder=os.getenv('BLENDER_OUTPUT_FOLDER', '/data/blender/blender-output')
blendFolder=os.getenv('BLENDER_FILES_FOLDER', '/data/blender/blender-files')

if not os.path.exists(outputFolder):
    os.makedirs(outputFolder)

if not os.path.exists(blendFolder):
    os.makedirs(blendFolder)

logging.basicConfig(format='time="%(asctime)s" level=%(levelname)s %(message)s', level=logging.INFO,datefmt="%Y-%m-%dT%H:%M:%S")

logging.info("starting rest api..")
logging.info("blendFolder=\"" + blendFolder+"\"")
logging.info("outputFolder=\"" + outputFolder+"\"")

class Handler(BaseHTTPRequestHandler):

    def do_GET(self):
        self.send_response(200)
        self.send_header('Content-type','application/json')
        self.end_headers()
        
        if (self.path == '/status'):
            message_dict = {'status': 'up','current-file': bpy.data.filepath}
            
        message = json.dumps(message_dict)
        self.wfile.write(bytes(message, "utf8"))
        
    def do_POST(self):
        content_len = int(self.headers.get('Content-Length'))
        post_body = self.rfile.read(content_len)
        input_dict = json.loads(post_body)

        if (self.path == '/open'):
            print("Opening file : " + json.dumps(input_dict))
            try:
                bpy.ops.wm.open_mainfile(filepath=blendFolder +"/"+ input_dict['fileName'])
                self.send_response(200)
                self.send_header('Content-type','application/json')
                self.end_headers()
                message_dict = {'status': 'ok'}
            except:
                self.send_response(500)
                self.send_header('Content-type','application/json')
                self.end_headers()
                message_dict = {'status': 'error'}
            

        
        if (self.path == '/render-region'):
            print("rendering region : " + json.dumps(input_dict))
            for scene in bpy.data.scenes:
                scene.render.resolution_percentage = 100
                scene.render.resolution_x = input_dict['resolutionX']
                scene.render.resolution_y = input_dict['resolutionY']
                scene.render.use_border = True
                scene.render.use_crop_to_border = True
                scene.render.border_min_x = float(input_dict['areaX'])/float(input_dict['frameDivider'])
                scene.render.border_min_y = float(input_dict['areaY'])/float(input_dict['frameDivider'])
                scene.render.border_max_x = float(input_dict['areaX']+1)/float(input_dict['frameDivider'])
                scene.render.border_max_y = float(input_dict['areaY']+1)/float(input_dict['frameDivider'])
                scene.render.image_settings.file_format=input_dict['fileFormat']
                scene.render.image_settings.color_depth=input_dict['colorDepth']
                scene.render.image_settings.color_mode=input_dict['colorMode']
                scene.render.use_compositing=input_dict['useComposition']
                
                # scene.render.tile_x=16
                # scene.render.tile_y=16
                
                scene.cycles.samples=input_dict['samples']
                scene.cycles.use_adaptive_sampling=input_dict['useAdaptiveSampling']
                scene.cycles.use_denoising=input_dict['useDenoising']
                
                # scene.view_settings.view_transform='Filmic Log'
                # scene.view_settings.look='High Contrast'

                # scene.render.use_save_buffers=True
                scene.render.use_persistent_data=False

                #scene.render.filepath = outputFolder + "/" + input_dict['outputPrefix'] +"_"+  str(input_dict['areaX']) + "_" + str(input_dict['areaY']) + ".png"
                outfname=input_dict['outputPrefix'] +"_"+ str(int(input_dict['frameDivider'])-input_dict['areaY']-1) + "_" + str(input_dict['areaX']) + ".png"
                scene.render.filepath = outputFolder + "/" + outfname
            bpy.ops.render.render(write_still=True)
            self.send_response(200)
            self.send_header('Content-type','application/json')
            self.end_headers()
            message_dict = {
                'status': 'ok',
                'minX': scene.render.border_min_x,
                'minY': scene.render.border_min_y,
                'maxX': scene.render.border_max_x,
                'maxY': scene.render.border_max_y,
                'filePath': outfname
                }
            
        message = json.dumps(message_dict)
        self.wfile.write(bytes(message, "utf8"))

    
def launch_server():
  server = HTTPServer(('', 8000), Handler)
  server.serve_forever()

def server_start():
  t = threading.Thread(target=launch_server)
  t.daemon = True
  t.start()
  
# server_start()
launch_server()