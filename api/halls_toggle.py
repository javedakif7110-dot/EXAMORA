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
        
        hall_id = data.get('hallId')
        for h in engine_instance.halls:
            if h["id"] == hall_id:
                h["status"] = "UNAVAILABLE" if h["status"] == "ACTIVE" else "ACTIVE"

        self.send_response(200)
        self.send_header('Content-Type', 'application/json')
        self.send_header('Access-Control-Allow-Origin', '*')
        self.end_headers()
        self.wfile.write(json.dumps({"status": "SUCCESS", "halls": engine_instance.halls}).encode('utf-8'))

    def do_GET(self):
        self.do_POST()
