from http.server import BaseHTTPRequestHandler
import json
import sys
import os
sys.path.append(os.path.dirname(__file__))
from _engine import engine_instance

class handler(BaseHTTPRequestHandler):
    def do_POST(self):
        length = int(self.headers.get('Content-Length', 0))
        body = self.rfile.read(length).decode('utf-8') if length > 0 else '{}'
        data = json.loads(body) if body else {}
        
        mode = data.get('mode', 'BENCHMARK')
        engine_instance.mode = mode
        engine_instance.reset_data()

        self.send_response(200)
        self.send_header('Content-Type', 'application/json')
        self.send_header('Access-Control-Allow-Origin', '*')
        self.end_headers()
        self.wfile.write(json.dumps({"status": "SUCCESS", "mode": mode}).encode('utf-8'))

    def do_GET(self):
        self.do_POST()
