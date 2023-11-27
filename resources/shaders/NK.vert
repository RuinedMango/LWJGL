#version 150

uniform mat4 ProjMTX;
in vec2 Position;
in vec2 TexCoord;
in vec4 Color;
out vec2 Frag_UV;
out vec4 Frag_Color;
void main(){
    Frag_UV = TexCoord;
    Frag_Color = Color;
    gl_Position = ProjMTX * vec4(Position.xy, 0, 1);
}