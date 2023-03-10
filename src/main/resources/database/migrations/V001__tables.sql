-- Create tables
CREATE TABLE public.item
(
    id   serial4 NOT NULL,
    name text    NOT NULL,
    CONSTRAINT item_pk PRIMARY KEY (id)
);

CREATE TABLE public.inventory
(
    id   serial4 NOT NULL,
    name text    NOT NULL,
    CONSTRAINT inventory_pk PRIMARY KEY (id)
);

CREATE TABLE public.inventory_stack
(
    inventory_id int NOT NULL,
    item_id      int NOT NULL,
    amount       int NOT null CHECK (amount > 0),
    CONSTRAINT inventory_stack_pk PRIMARY KEY (inventory_id, item_id),
    CONSTRAINT inventory_stack_inventory_fk
        FOREIGN KEY (inventory_id)
            REFERENCES inventory (id),
    CONSTRAINT inventory_stack_item_fk
        FOREIGN KEY (item_id)
            REFERENCES item (id)
);

CREATE TABLE public.recipe
(
    id   serial4 NOT NULL,
    name text    NOT NULL,
    CONSTRAINT recipe_pk PRIMARY KEY (id)
);

CREATE TABLE public.recipe_input
(
    recipe_id int NOT NULL,
    item_id   int NOT NULL,
    amount    int NOT null CHECK (amount > 0),
    CONSTRAINT recipe_input_pk PRIMARY KEY (recipe_id, item_id),
    CONSTRAINT recipe_input_recipe_fk
        FOREIGN KEY (recipe_id)
            REFERENCES recipe (id),
    CONSTRAINT recipe_input_item_fk
        FOREIGN KEY (item_id)
            REFERENCES item (id)
);

CREATE TABLE public.recipe_output
(
    recipe_id int NOT NULL,
    item_id   int NOT NULL,
    amount    int NOT null CHECK (amount > 0),
    CONSTRAINT recipe_output_pk PRIMARY KEY (recipe_id, item_id),
    CONSTRAINT recipe_output_recipe_fk
        FOREIGN KEY (recipe_id)
            REFERENCES recipe (id),
    CONSTRAINT recipe_output_item_fk
        FOREIGN KEY (item_id)
            REFERENCES item (id)
);
