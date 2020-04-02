
import org.apache.spark.util.AccumulatorV2

class MyComplex(var x:Int , var y:Int) extends Serialization
{
	
	def reset():Unit = {
		x = 0
		y = 0
	}

	def add(p : MyComplex):MyComplex = {
		x = x + p.get_X()
		y = y + p.get_Y()
	}

	def get_X():Int = {
		x
	}

	def get_Y():Int = {
		y
	}
}

object ComplexAccumulatorV2 extends AccumulatorV2[MyComplex, MyComplex] {
    private val myc:MyComplex = new MyComplex(0,0)

    def reset(): Unit = {
        myc.reset()
    }

    def add(v: MyComplex): Unit = {
        myc.add(v)
    }
    def value():MyComplex = {
        return myc
    }
    def isZero(): Boolean = {
        return (myc.x == 0 && myc.y == 0)
    }
    def copy():AccumulatorV2[MyComplex, MyComplex] = {
        return ComplexAccumulatorV2
    }
    def merge(other:AccumulatorV2[MyComplex, MyComplex]) = {
        myc.add(other.value)
    }
}

sc.register(ComplexAccumulatorV2, "somename")

var ca = ComplexAccumulatorV2

var rdd = sc.parallelize(1 to 10)
var res = rdd.map(x => ca.add(new MyComplex(x,x)))
res.count
ca.value.get_X()
ca.value.get_Y()
