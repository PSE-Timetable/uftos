<script lang="ts">
  import { Pagination as PaginationPrimitive } from 'bits-ui';
  import { cn } from '$lib/utils.js';
  import { type Props, buttonVariants } from '$lib/elements/ui/button/index.js';

  type $$Props = PaginationPrimitive.PageProps & Props;

  type $$Events = PaginationPrimitive.PageEvents;

  let className: $$Props['class'] = undefined;
  export let page: $$Props['page'];
  export let size: $$Props['size'] = 'icon';

  export { className as class };
</script>

<PaginationPrimitive.Page
  bind:page
  class={cn(
    buttonVariants({
      variant: 'secondary',
      size,
    }),
    className,
  )}
  {...$$restProps}
  on:click
>
  <slot>{page.value}</slot>
</PaginationPrimitive.Page>
