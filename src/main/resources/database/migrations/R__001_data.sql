-- Insert data
INSERT INTO public.item(name)
VALUES ('Item 1'),
       ('Item 2'),
       ('Item 3'),
       ('Item 4');

INSERT INTO public.inventory(name)
VALUES ('Inventory 1'),
       ('Inventory 2'),
       ('Inventory 3');

INSERT INTO public.inventory_stack(inventory_id, item_id, amount)
VALUES (1, 1, 1),
       (1, 2, 2),
       (1, 3, 3);

INSERT INTO public.recipe(name)
VALUES ('Recipe 1'),
       ('Recipe 2');

INSERT INTO public.recipe_input(recipe_id, item_id, amount)
VALUES (1, 1, 1),
       (1, 2, 1),
       (2, 1, 2),
       (2, 3, 1);

INSERT INTO public.recipe_output(recipe_id, item_id, amount)
VALUES (1, 3, 1),
       (2, 4, 1);
