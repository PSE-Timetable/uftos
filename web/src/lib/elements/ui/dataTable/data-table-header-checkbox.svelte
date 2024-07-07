<script lang="ts">
    import { Checkbox } from '$lib/elements/ui/checkbox';
    import { cn } from '$lib/utils';
    import type { Writable } from 'svelte/store';
  
    export let checked: Writable<boolean>;
  </script>
  
  <Checkbox bind:checked={$checked} class={cn("border-white data-[state=checked]:bg-white data-[state=checked]:text-foreground")}/>