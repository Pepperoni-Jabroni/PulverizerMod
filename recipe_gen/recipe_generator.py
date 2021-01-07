import json
import sys

BASIC_RECIPE_NAME_TEMPL = '{recipe_ingredient}_{recipe_result}'
BASIC_RECIPE_FLNAME_TEMPL = 'pulverizer_{recipe_name}.json'
BASIC_RECIPE_TEMPL = """
{{
  \"type\": \"pulverizer_mod:pulverizer\",
  \"id\": \"pulverizer_mod:{recipe_name}\",
  \"ingredient\": \"minecraft:{recipe_ingredient}\",
  \"result\": {{
    \"id\": \"minecraft:{recipe_result}\",
    \"Count\": {recipe_count}
  }},
  \"pulverizetime\": 200,
  \"category\": \"{category}\"
}}
"""

if __name__ == '__main__':
   if len(sys.argv) == 3:
      processing_file = sys.argv[1]
      with open(processing_file, 'r') as processing_f:
         processing_info = json.loads(processing_f.read())
         output_dir = sys.argv[2]
         # Handle basic recipes
         for recipe_arr in processing_info['basic_recipes']:
            recipe_ingredient, recipe_result, recipe_count, category = recipe_arr
            recipe_name = BASIC_RECIPE_NAME_TEMPL.format(recipe_ingredient=recipe_ingredient, recipe_result=recipe_result)
            with open(output_dir+BASIC_RECIPE_FLNAME_TEMPL.format(recipe_name=recipe_name), 'w') as f:
               print('Generated '+output_dir+BASIC_RECIPE_FLNAME_TEMPL.format(recipe_name=recipe_name))
               f.write(BASIC_RECIPE_TEMPL.format(recipe_name=recipe_name, recipe_ingredient=recipe_ingredient, recipe_result=recipe_result, recipe_count=recipe_count, category=category))
         # Handle dye recipes
         category="dye"
         for dye in processing_info['dyes']:
            dye_item = dye+'_dye'
            for dye_block in processing_info['dye_blocks']:
               recipe_name = BASIC_RECIPE_NAME_TEMPL.format(recipe_ingredient=dye+'_'+dye_block,recipe_result=dye_item)
               with open(output_dir+BASIC_RECIPE_FLNAME_TEMPL.format(recipe_name=recipe_name), 'w') as f:
                  print('Generated '+output_dir+BASIC_RECIPE_FLNAME_TEMPL.format(recipe_name=recipe_name))
                  f.write(BASIC_RECIPE_TEMPL.format(recipe_name=recipe_name, recipe_ingredient=dye+'_'+dye_block, recipe_result=dye_item, recipe_count=1, category=category))
         # Handle ingot recipes
         category="recycle"
         for ingot in processing_info['ingot_types']:
            ingot_item = ingot+'_ingot'
            for source in processing_info['ingot_sources']:
               source_item = ingot+'_'+source
               if ingot == 'gold':
                  source_item = ingot+'en_'+source
               recipe_name = BASIC_RECIPE_NAME_TEMPL.format(recipe_ingredient=source_item,recipe_result=ingot_item)
               with open(output_dir+BASIC_RECIPE_FLNAME_TEMPL.format(recipe_name=recipe_name), 'w') as f:
                  print('Generated '+output_dir+BASIC_RECIPE_FLNAME_TEMPL.format(recipe_name=recipe_name))
                  f.write(BASIC_RECIPE_TEMPL.format(recipe_name=recipe_name, recipe_ingredient=source_item, recipe_result=ingot_item, recipe_count=1, category=category))
         # Handle ore recipes
         category="ore"
         for ore_arr in processing_info['ores']:
            item, count = ore_arr
            ore = item + '_ore'
            if item in ['gold', 'iron']:
               item = item + '_ingot'
            if item == 'lapis':
               item = item + '_lazuli'
            recipe_name = BASIC_RECIPE_NAME_TEMPL.format(recipe_ingredient=ore, recipe_result=item)
            with open(output_dir+BASIC_RECIPE_FLNAME_TEMPL.format(recipe_name=recipe_name), 'w') as f:
               print('Generated '+output_dir+BASIC_RECIPE_FLNAME_TEMPL.format(recipe_name=recipe_name))
               f.write(BASIC_RECIPE_TEMPL.format(recipe_name=recipe_name, recipe_ingredient=ore, recipe_result=item, recipe_count=count, category=category))
