<script lang="ts">
	import {Tabs as TabsPrimitive} from "bits-ui";
	import {cn} from "$lib/utils.js";

	type $$Props = TabsPrimitive.ListProps;

    let className: $$Props["class"] = undefined;
    export {className as class};
</script>

<TabsPrimitive.List
        {...$$restProps}
        class={cn(
		"d inline-flex h-10 items-center justify-center rounded-md bg-white p-1 text-muted-foreground",
		className
	)}
>
    <slot/>
</TabsPrimitive.List>
