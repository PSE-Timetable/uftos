<script lang="ts">
  import type { HTMLInputAttributes } from 'svelte/elements';
  import type { InputEvents } from './index.js';
  import { cn } from '$lib/utils.js';

  type $$Props = HTMLInputAttributes & {background?: boolean};
  type $$Events = InputEvents;

  let className: $$Props['class'] = undefined;
  export let value: $$Props['value'] = undefined;
  export { className as class };

  // Workaround for https://github.com/sveltejs/svelte/issues/9305
  // Fixed in Svelte 5, but not backported to 4.x.
  export let readonly: $$Props['readonly'] = undefined;
  export let background: boolean = false;
</script>

<input
  class={cn(
    'flex w-full rounded-md px-4 py-3 border-input text-primary text-sm ring-offset-background file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50',
    background ? 'shadow-customSmall bg-white' : 'bg-background',
    className,
  )}
  bind:value
  {readonly}
  on:blur
  on:change
  on:click
  on:focus
  on:focusin
  on:focusout
  on:keydown
  on:keypress
  on:keyup
  on:mouseover
  on:mouseenter
  on:mouseleave
  on:mousemove
  on:paste
  on:input
  on:wheel|passive
  {...$$restProps}
/>
