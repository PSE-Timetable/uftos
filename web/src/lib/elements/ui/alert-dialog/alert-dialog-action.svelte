<script lang="ts">
  import { AlertDialog as AlertDialogPrimitive } from 'bits-ui';
  import { buttonVariants } from '$lib/elements/ui/button/index.js';
  import { cn } from '$lib/utils.js';

  type $$Props = AlertDialogPrimitive.ActionProps;
  type $$Events = AlertDialogPrimitive.ActionEvents;

  let className: $$Props['class'] = undefined;
  export { className as class };
</script>

<AlertDialogPrimitive.Action
  class={cn(buttonVariants({ variant: 'secondary' }), className)}
  {...$$restProps}
  on:click
  on:keydown
  let:builder
>
  <slot {builder} />
</AlertDialogPrimitive.Action>
