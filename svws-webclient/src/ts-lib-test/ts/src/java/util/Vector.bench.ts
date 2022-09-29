import { bench, describe } from "vitest";
import { Vector } from "~/index";
import { l, n, s } from "../../shared/TestObjects";

let v: Vector<unknown>;

describe.each([s, n, l])(
	"java.util.Vector, getestet mit $name",
	({ a, b, c, d, e }) => {
			v = new Vector();
			v.add(a);
			v.add(b);
			v.add(c);
			v.add(d);
			v.add(e);
		bench( "Array from", () => {
				Array.from(v);
			});
		bench( "toArray", () => {
				v.toArray(new Array<typeof a>());
			});
	}
);
