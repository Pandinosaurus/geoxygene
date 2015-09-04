#version 400

uniform float globalOpacity = 1;
uniform float objectOpacity = 1;
uniform sampler2D bufferImage;

uniform int typeColormap = 0;
uniform int nbPointsColormap = 0;
uniform sampler2D bufferColormap;

uniform int time = 0;
uniform int animate = 0;

in VertexData {
    vec4 color;
    vec2 textureUV;
} fragmentIn;

out vec4 outColor;

vec4 interpolateColor(vec4 pixel) 
{
    int prev = -1;
    int next = -1;

    vec4 pixel_value = vec4(pixel.x,pixel.x,pixel.x,pixel.x);
    for(int i=0; i<nbPointsColormap-1; i++)
    {
        vec4 value_i = texelFetch(bufferColormap,ivec2(i,1),0);
        vec4 value_i_1 = texelFetch(bufferColormap,ivec2(i+1,1),0);
        
        if ((pixel_value.x >= value_i.x)&&(pixel_value.x<=value_i_1.x )) 
        {
            prev = i;
            next = i+1;
            
            vec4 color_prev = texelFetch(bufferColormap,ivec2(prev,0),0);
            vec4 color_next = texelFetch(bufferColormap,ivec2(next,0),0);
            vec4 value_prev = texelFetch(bufferColormap,ivec2(prev,1),0);
            vec4 value_next = texelFetch(bufferColormap,ivec2(next,1),0);

            return vec4( (color_prev + (pixel_value-value_prev)*(color_next - color_prev)/(value_next-value_prev)) / vec4(255.0,255.0,255.0,255.0));
        }
    }
    if(pixel.x<texelFetch(bufferColormap,ivec2(0,1),0).x)
    {
        return texelFetch(bufferColormap,ivec2(0,0),0) / vec4(255.0,255.0,255.0,255.0);
    }
    else 
    {
        return vec4(0.0,0.0,0.0,0.0);
    }
}

vec4 categorizeColor(vec4 pixel) 
{
    vec4 pixel_value = vec4(pixel.x,pixel.x,pixel.x,pixel.x);
    
    if(pixel_value.x<texelFetch(bufferColormap,ivec2(0,1),0).x) {
        return (texelFetch(bufferColormap,ivec2(0,0),0)  / vec4(255.0,255.0,255.0,255.0));
    }  
    
    for(int i=1; i<nbPointsColormap; i++)
    {        
        if (pixel_value.x <= texelFetch(bufferColormap,ivec2(i,1),0).x)
        {
            return (texelFetch(bufferColormap,ivec2(i,0),0)  / vec4(255.0,255.0,255.0,255.0));    
        }
    }
    return vec4(0.0,0.0,0.0,0.0);
}


void main(void) 
{
    vec2 P = fragmentIn.textureUV;
    
    // The raster goes in the rectangle 
    vec4 pixel = texture(bufferImage,P);                  
    
    if (typeColormap == 1) {
        // Colormap with interpolation 
        // Animation, test tide simulation
        if(animate==1)
        {
            //mean_height = 4.75;
            //range = 3.25;
            float mean_height = 0.57;
            float range = 1.7;
            pixel.x = pixel.x - mean_height + (sin( mod(time,10000) /10000.0*2*3.14116)*range);
        }
        // color interpolation with colormap
        outColor = interpolateColor(pixel);
     } else if ( typeColormap == 2 ) {
        // Colormap with categorize
        // Animation, test tide simulation
        if(animate==1)
        {
            pixel.x = pixel.x - 4.75 + (sin( mod(time,10000) /10000.0*2*3.14116)*3.25);
        }
        outColor = categorizeColor(pixel);
        
     } 
     else
     {
        outColor = pixel;
     }
     
    // Opacity
    outColor.a = outColor.a*globalOpacity*objectOpacity;
}